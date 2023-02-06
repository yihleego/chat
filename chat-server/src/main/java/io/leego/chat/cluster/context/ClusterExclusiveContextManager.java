package io.leego.chat.cluster.context;

import com.google.protobuf.Message;
import io.leego.chat.constant.Codes;
import io.leego.chat.core.Box;
import io.leego.chat.core.Meta;
import io.leego.chat.core.Packet;
import io.leego.chat.manager.ChatManager;
import io.leego.chat.manager.GroupManager;
import io.leego.chat.server.context.AbstractExclusiveContextManager;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * For cluster.
 *
 * @author Leego Yih
 */
public class ClusterExclusiveContextManager extends AbstractExclusiveContextManager implements ClusterContextManager {
    private static final Logger logger = LoggerFactory.getLogger(ClusterExclusiveContextManager.class);
    private final ChatManager chatManager;
    private final ConcurrentMap<Integer, ChannelHandlerContext> nodes;

    public ClusterExclusiveContextManager(int initialCapacity, int threads, ChatManager chatManager, GroupManager groupManager) {
        super(initialCapacity, threads, groupManager);
        this.chatManager = chatManager;
        this.nodes = new ConcurrentHashMap<>(64);
    }

    @Override
    public void register(ChannelHandlerContext ctx, Integer node) {
        nodes.put(node, ctx);
    }

    @Override
    public void unregister(ChannelHandlerContext ctx, Integer node) {
        nodes.remove(node, ctx);
    }

    @Override
    public void forward(Packet packet) {
        Box box = packet.getBox();
        List<Long> targets = packet.getTargetList();
        for (Long target : targets) {
            ChannelHandlerContext ctx = getLocal(target);
            if (ctx == null) {
                logger.error("Forward failed, no context for [{}]", target);
                return;
            }
            if (box.getCode() == Codes.KICKED_OUT) {
                int channelId = Integer.parseInt(toString(box.getData()));
                if (channelId == getChannelId(ctx) && removeLocal(ctx)) {
                    kickOut(ctx);
                }
            } else {
                ctx.writeAndFlush(box);
            }
        }
    }

    @Override
    public void set(ChannelHandlerContext ctx) {
        // Set to Local
        setLocal(ctx);
        // Set to Remote
        executorService.execute(() -> {
            Long userId = getUserId(ctx);
            int channelId = getChannelId(ctx);
            Meta old = chatManager.setExclusiveMeta(userId, channelId);
            if (old == null) {
                return;
            }
            ChannelHandlerContext node = nodes.get(old.getNode());
            if (node == null) {
                return;
            }
            logger.info("Try to kicked remote client out [{}] {}", userId, channelId);
            Box box = toBox(Codes.KICKED_OUT, Integer.toString(old.getChannel()));
            node.writeAndFlush(toPacket(userId, box));
        });
    }

    @Override
    public void remove(ChannelHandlerContext ctx) {
        if (isKickedOut(ctx)) {
            logger.info("Client has been kicked out {}", ctx.channel());
            return;
        }
        // Remove from Local
        removeLocal(ctx);
        // Remove from Remote
        executorService.execute(() -> chatManager.removeExclusiveMeta(getUserId(ctx), getChannelId(ctx)));
    }

    @Override
    public void sendToUser(Long userId, int code, Message.Builder data, Consumer<Integer> callback) {
        // Send to Local
        ChannelHandlerContext ctx = getLocal(userId);
        if (ctx != null) {
            ctx.writeAndFlush(toBox(code, data));
            accept(callback, 1);
            return;
        }
        // Send to Remote
        executorService.execute(() -> {
            Meta meta = chatManager.getExclusiveMeta(userId);
            if (meta == null) {
                accept(callback, 0);
                return;
            }
            ChannelHandlerContext node = nodes.get(meta.getNode());
            if (node == null) {
                accept(callback, 0);
                return;
            }
            node.writeAndFlush(toPacket(userId, toBox(code, data)));
            accept(callback, 1);
        });
    }

    @Override
    public void sendToGroup(Long sender, Long groupId, int code, Message.Builder data, Consumer<Integer> callback) {
        executorService.execute(() -> {
            int count = 0;
            Box box = null; // lazy
            List<Long> userIds = groupManager.getMembers(groupId);
            List<Long> remains = new ArrayList<>();
            // Send to Local
            for (Long userId : userIds) {
                if (userId.equals(sender)) {
                    continue;
                }
                ChannelHandlerContext ctx = getLocal(userId);
                if (ctx == null) {
                    remains.add(userId);
                    continue;
                }
                if (box == null) {
                    box = toBox(code, data);
                }
                ctx.writeAndFlush(box);
                count++;
            }
            // Send to Remote
            if (remains.isEmpty() || nodes.isEmpty()) {
                accept(callback, count);
                return;
            }
            // node -> users
            Map<Integer, List<Long>> metas = chatManager.getExclusiveMetas(remains)
                    .collect(Collectors.groupingBy(Meta::getNode, Collectors.mapping(Meta::getUser, Collectors.toList())));
            if (metas.isEmpty()) {
                accept(callback, count);
                return;
            }
            for (Map.Entry<Integer, List<Long>> entry : metas.entrySet()) {
                ChannelHandlerContext node = nodes.get(entry.getKey());
                if (node == null) {
                    continue;
                }
                if (box == null) {
                    box = toBox(code, data);
                }
                List<Long> targets = entry.getValue();
                node.writeAndFlush(toPacket(targets, box));
                count += targets.size();
            }
            accept(callback, count);
        });
    }
}
