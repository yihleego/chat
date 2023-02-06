package io.leego.chat.cluster.context;

import io.leego.chat.core.Packet;
import io.leego.chat.server.context.ContextManager;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Leego Yih
 */
public interface ClusterContextManager extends ContextManager {

    void register(ChannelHandlerContext ctx, Integer node);

    void unregister(ChannelHandlerContext ctx, Integer node);

    void forward(Packet packet);

}
