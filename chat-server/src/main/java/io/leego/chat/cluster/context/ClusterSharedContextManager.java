package io.leego.chat.cluster.context;

import com.google.protobuf.Message;
import com.google.protobuf.TextFormat;
import io.leego.chat.constant.Codes;
import io.leego.chat.core.Box;
import io.leego.chat.core.Meta;
import io.leego.chat.core.Packet;
import io.leego.chat.manager.ChatManager;
import io.leego.chat.manager.GroupManager;
import io.leego.chat.server.context.AbstractSharedContextManager;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * For cluster.
 *
 * @author Leego Yih
 */
public class ClusterSharedContextManager extends AbstractSharedContextManager implements ClusterContextManager {
    private static final Logger logger = LoggerFactory.getLogger(ClusterSharedContextManager.class);
    private final ChatManager chatManager;
    private final ConcurrentMap<Integer, ChannelHandlerContext> nodes;

    public ClusterSharedContextManager(int initialCapacity, int threads, ChatManager chatManager, GroupManager groupManager) {
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
        List<Integer> clients = packet.getClientList();
        if (targets.size() != clients.size()) {
            logger.error("Invalid length: {}", TextFormat.shortDebugString(packet));
            return;
        }
        for (int i = 0, len = targets.size(); i < len; i++) {
            ChannelHandlerContext ctx = getLocal(targets.get(i), clients.get(i));
            if (ctx == null) {
                logger.error("Forward failed, no context for [{}-{}]", targets.get(i), clients.get(i));
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
            Integer client = getClient(ctx);
            int channelId = getChannelId(ctx);
            Meta old = chatManager.setSharedMeta(userId, client, channelId);
            if (old == null) {
                return;
            }
            ChannelHandlerContext node = nodes.get(old.getNode());
            if (node == null) {
                return;
            }
            logger.info("Try to kicked remote client out [{}-{}] {}", userId, client, channelId);
            Box box = toBox(Codes.KICKED_OUT, Integer.toString(old.getChannel()));
            node.writeAndFlush(toPacket(userId, client, box));
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
        executorService.execute(() -> chatManager.removeSharedMeta(getUserId(ctx), getClient(ctx), getChannelId(ctx)));
    }

    @Override
    public void sync(ChannelHandlerContext self, int code, Message.Builder data) {
        executorService.execute(() -> {
            Long userId = getUserId(self);
            Box box = null; // lazy
            // Send to Local
            ChannelHandlerContext[] array = getLocals(userId);
            if (array != null) {
                for (ChannelHandlerContext ctx : array) {
                    if (ctx == self) {
                        continue;
                    }
                    if (box == null) {
                        box = toBox(code, data);
                    }
                    ctx.writeAndFlush(box);
                }
            }
            // Send to Remote
            if (nodes.isEmpty()) {
                return;
            }
            List<Meta> metas = chatManager.getSharedMeta(userId);
            if (metas.isEmpty()) {
                return;
            }
            for (Meta meta : metas) {
                ChannelHandlerContext node = nodes.get(meta.getNode());
                if (node == null) {
                    continue;
                }
                if (box == null) {
                    box = toBox(code, data);
                }
                node.writeAndFlush(toPacket(userId, meta.getClient(), box));
            }
        });
    }

    @Override
    public void sendToUser(Long userId, int code, Message.Builder data, Consumer<Integer> callback) {
        executorService.execute(() -> {
            int count = 0;
            Box box = null; // lazy
            // Send to Local
            ChannelHandlerContext[] array = getLocals(userId);
            if (array != null) {
                box = toBox(code, data);
                for (ChannelHandlerContext ctx : array) {
                    ctx.writeAndFlush(box);
                }
                count = array.length;
            }
            // Send to Remote
            if (nodes.isEmpty()) {
                accept(callback, count);
                return;
            }
            List<Meta> metas = chatManager.getSharedMeta(userId);
            if (metas.isEmpty()) {
                accept(callback, count);
                return;
            }
            for (Meta meta : metas) {
                ChannelHandlerContext node = nodes.get(meta.getNode());
                if (node == null) {
                    continue;
                }
                if (box == null) {
                    box = toBox(code, data);
                }
                node.writeAndFlush(toPacket(userId, meta.getClient(), box));
                count++;
            }
            accept(callback, count);
        });
    }

    @Override
    public void sendToGroup(Long sender, Long groupId, int code, Message.Builder data, Consumer<Integer> callback) {
        executorService.execute(() -> {
            int count = 0;
            Box box = null; // lazy
            List<Long> userIds = groupManager.getMembers(groupId);
            // Send to Local
            for (Long userId : userIds) {
                if (userId.equals(sender)) {
                    continue;
                }
                ChannelHandlerContext[] array = getLocals(userId);
                if (array != null) {
                    if (box == null) {
                        box = toBox(code, data);
                    }
                    for (ChannelHandlerContext ctx : array) {
                        ctx.writeAndFlush(box);
                    }
                    count += array.length;
                }
            }
            // Send to Remote
            if (nodes.isEmpty()) {
                accept(callback, count);
                return;
            }
            List<Meta> metas = chatManager.getSharedMetas(userIds);
            if (metas.isEmpty()) {
                accept(callback, count);
                return;
            }
            Set<Integer> nodeSet = new HashSet<>();
            Map<Integer, List<Long>> targetMap = new HashMap<>();
            Map<Integer, List<Integer>> clientMap = new HashMap<>();
            for (Meta meta : metas) {
                nodeSet.add(meta.getNode());
                targetMap.computeIfAbsent(meta.getNode(), (key) -> new ArrayList<>()).add(meta.getUser());
                clientMap.computeIfAbsent(meta.getNode(), (key) -> new ArrayList<>()).add(meta.getClient());
            }
            for (Integer n : nodeSet) {
                ChannelHandlerContext node = nodes.get(n);
                if (node == null) {
                    continue;
                }
                if (box == null) {
                    box = toBox(code, data);
                }
                List<Long> targets = targetMap.get(n);
                List<Integer> clients = clientMap.get(n);
                node.writeAndFlush(toPacket(targets, clients, box));
                count += targets.size();
            }
            accept(callback, count);
        });
    }
}
