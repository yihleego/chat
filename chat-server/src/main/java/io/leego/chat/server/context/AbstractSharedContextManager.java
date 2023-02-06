package io.leego.chat.server.context;

import io.leego.chat.manager.GroupManager;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * Allow multiple clients online for each user,
 * the most recently connected client will kick out the old same one.
 *
 * @author Leego Yih
 */
public abstract class AbstractSharedContextManager extends AbstractContextManager {
    private final ConcurrentMap<Long, CopyOnWriteContextGroup> contexts;
    private final LongAdder size;

    public AbstractSharedContextManager(int initialCapacity, int threads, GroupManager groupManager) {
        // TODO [OOM] Should not resize
        super(threads, groupManager);
        this.contexts = new ConcurrentHashMap<>(initialCapacity, 1);
        this.size = new LongAdder();
    }

    @Override
    public int size() {
        return size.intValue();
    }

    protected ChannelHandlerContext[] getLocals(Long userId) {
        CopyOnWriteContextGroup group = contexts.get(userId);
        if (group == null || !group.isAvailable()) {
            return null;
        }
        return group.getArray();
    }

    protected ChannelHandlerContext getLocal(Long userId, Integer client) {
        CopyOnWriteContextGroup group = contexts.get(userId);
        if (group == null || !group.isAvailable()) {
            return null;
        }
        return group.get(client);
    }

    protected ChannelHandlerContext setLocal(ChannelHandlerContext ctx) {
        Long userId = getUserId(ctx);
        Integer client = getClient(ctx);
        ChannelHandlerContext old;
        while (true) {
            CopyOnWriteContextGroup group = contexts.compute(userId,
                    (k, v) -> v != null && v.isAvailable() ? v : new CopyOnWriteContextGroup());
            try {
                old = group.set(ctx, client);
                break;
            } catch (IllegalStateException ignored) {
                // It is unavailable, Try again
            }
        }
        if (old != null && old != ctx) {
            kickOut(old);
        } else {
            size.increment();
        }
        return old;
    }

    protected boolean removeLocal(ChannelHandlerContext ctx) {
        Long userId = getUserId(ctx);
        CopyOnWriteContextGroup group = contexts.get(userId);
        if (group != null && group.isAvailable()) {
            if (group.remove(ctx)) {
                size.decrement();
                // Remove the group if it is unavailable, prevent OOM
                if (!group.isAvailable()) {
                    contexts.remove(userId, group);
                }
                return true;
            }
        }
        return false;
    }
}
