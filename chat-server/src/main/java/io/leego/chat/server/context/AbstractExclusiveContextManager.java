package io.leego.chat.server.context;

import com.google.protobuf.Message;
import io.leego.chat.manager.GroupManager;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Allow only one client online for each user,
 * the most recently connected client will kick out the old one.
 *
 * @author Leego Yih
 */
public abstract class AbstractExclusiveContextManager extends AbstractContextManager {
    private final ConcurrentMap<Long, ChannelHandlerContext> contexts;

    public AbstractExclusiveContextManager(int initialCapacity, int threads, GroupManager groupManager) {
        // TODO [OOM] Should not resize
        super(threads, groupManager);
        this.contexts = new ConcurrentHashMap<>(initialCapacity, 1);
    }

    @Override
    public int size() {
        return contexts.size();
    }

    @Override
    public void sync(ChannelHandlerContext ctx, int code, Message.Builder data) {
        // Noting to sync
    }

    protected ChannelHandlerContext getLocal(Long userId) {
        return contexts.get(userId);
    }

    protected ChannelHandlerContext setLocal(ChannelHandlerContext ctx) {
        ChannelHandlerContext old = contexts.put(getUserId(ctx), ctx);
        if (old != null && old != ctx) {
            kickOut(old);
        }
        return old;
    }

    protected boolean removeLocal(ChannelHandlerContext ctx) {
        return contexts.remove(getUserId(ctx), ctx);
    }
}
