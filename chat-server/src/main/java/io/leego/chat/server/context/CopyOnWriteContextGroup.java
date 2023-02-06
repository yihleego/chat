package io.leego.chat.server.context;

import io.leego.chat.constant.AttributeKeys;
import io.netty.channel.ChannelHandlerContext;

/**
 * Copy-on-write context group.
 *
 * @author Leego Yih
 */
public class CopyOnWriteContextGroup {
    private static final ChannelHandlerContext[] EMPTY = new ChannelHandlerContext[0];
    private transient volatile ChannelHandlerContext[] array;

    public CopyOnWriteContextGroup() {
        this.array = EMPTY;
    }

    public ChannelHandlerContext[] getArray() {
        return this.array;
    }

    public ChannelHandlerContext get(int client) {
        ChannelHandlerContext[] curArray = this.array;
        if (curArray == null) {
            return null;
        }
        for (ChannelHandlerContext ctx : curArray) {
            if (getClient(ctx) == client) {
                return ctx;
            }
        }
        return null;
    }

    public synchronized ChannelHandlerContext set(ChannelHandlerContext ctx, int client) {
        ChannelHandlerContext[] curArray = this.array;
        if (curArray == null) {
            throw new IllegalStateException("Unavailable");
        }
        ChannelHandlerContext oldCtx = null;
        int i = 0;
        int len = curArray.length;
        // Find the context with the same client type
        for (; i < len; i++) {
            ChannelHandlerContext o = curArray[i];
            if (getClient(o) == client) {
                oldCtx = o;
                break;
            }
        }
        ChannelHandlerContext[] newArray;
        if (oldCtx == null) {
            // Append
            newArray = new ChannelHandlerContext[len + 1];
            System.arraycopy(curArray, 0, newArray, 0, len);
            newArray[len] = ctx;
        } else {
            // Replace
            newArray = curArray.clone();
            newArray[i] = ctx;
        }
        this.array = newArray;
        return oldCtx;
    }

    public synchronized boolean remove(ChannelHandlerContext ctx) {
        ChannelHandlerContext[] curArray = this.array;
        if (curArray == null) {
            throw new IllegalStateException("Unavailable");
        }
        ChannelHandlerContext oldCtx = null;
        int i = 0;
        int len = curArray.length;
        // Find the context
        for (; i < len; i++) {
            ChannelHandlerContext o = curArray[i];
            if (o == ctx) {
                oldCtx = o;
                break;
            }
        }
        if (oldCtx == null) {
            // Do nothing if not exist
            return false;
        }
        if (len == 1) {
            // Make unavailable, it will be removed
            this.array = null;
            return true;
        }
        int newLen = len - 1;
        ChannelHandlerContext[] newArray = new ChannelHandlerContext[newLen];
        if (i == 0) {
            // Remove first
            System.arraycopy(curArray, 1, newArray, 0, newLen);
        } else if (i == newLen) {
            // Remove last
            System.arraycopy(curArray, 0, newArray, 0, newLen);
        } else {
            // Remove middle
            System.arraycopy(curArray, 0, newArray, 0, i);
            System.arraycopy(curArray, i + 1, newArray, i, newLen - i);
        }
        this.array = newArray;
        return true;
    }

    public int size() {
        Object[] a = this.array;
        return a != null ? a.length : 0;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean isAvailable() {
        return array != null;
    }

    private Integer getClient(ChannelHandlerContext ctx) {
        return ctx.channel().attr(AttributeKeys.CLIENT).get();
    }
}
