package io.leego.chat.server.context;

import com.google.protobuf.Message;
import io.leego.chat.core.Box;
import io.leego.chat.manager.GroupManager;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;

/**
 * For standalone.
 *
 * @author Leego Yih
 */
public class StandaloneSharedContextManager extends AbstractSharedContextManager {
    private static final Logger logger = LoggerFactory.getLogger(StandaloneSharedContextManager.class);

    public StandaloneSharedContextManager(int initialCapacity, int threads, GroupManager groupManager) {
        super(initialCapacity, threads, groupManager);
    }

    @Override
    public void set(ChannelHandlerContext ctx) {
        setLocal(ctx);
    }

    @Override
    public void remove(ChannelHandlerContext ctx) {
        if (isKickedOut(ctx)) {
            logger.info("Client has been kicked out {}", ctx.channel());
            return;
        }
        removeLocal(ctx);
    }

    @Override
    public void sync(ChannelHandlerContext self, int code, Message.Builder data) {
        ChannelHandlerContext[] array = getLocals(getUserId(self));
        if (array == null || array.length == 0) {
            return;
        }
        Box box = null; // lazy
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

    @Override
    public void sendToUser(Long userId, int code, Message.Builder data, Consumer<Integer> callback) {
        ChannelHandlerContext[] array = getLocals(userId);
        if (array == null) {
            accept(callback, 0);
            return;
        }
        Box box = toBox(code, data);
        for (ChannelHandlerContext ctx : array) {
            ctx.writeAndFlush(box);
        }
        accept(callback, array.length);
    }

    @Override
    public void sendToGroup(Long sender, Long groupId, int code, Message.Builder data, Consumer<Integer> callback) {
        executorService.execute(() -> {
            int count = 0;
            Box box = null; // lazy
            List<Long> userIds = groupManager.getMembers(groupId);
            for (Long userId : userIds) {
                if (userId.equals(sender)) {
                    continue;
                }
                ChannelHandlerContext[] array = getLocals(userId);
                if (array == null) {
                    continue;
                }
                if (box == null) {
                    box = toBox(code, data);
                }
                for (ChannelHandlerContext ctx : array) {
                    ctx.writeAndFlush(box);
                }
                count += array.length;
            }
            accept(callback, count);
        });
    }
}
