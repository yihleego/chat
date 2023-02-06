package io.leego.chat.cluster.handler;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import io.leego.chat.cluster.context.ClusterContextManager;
import io.leego.chat.core.Box;
import io.leego.chat.core.Packet;
import io.leego.chat.manager.ChatManager;
import io.leego.chat.util.IntUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Leego Yih
 */
@ChannelHandler.Sharable
public class ClusterRegistryClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ClusterRegistryClientHandler.class);
    private final ChatManager chatManager;
    private final ClusterContextManager contextManager;

    public ClusterRegistryClientHandler(ChatManager chatManager, ClusterContextManager contextManager) {
        this.chatManager = chatManager;
        this.contextManager = contextManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.debug("Leader node connected {}", ctx.channel());
        int node = chatManager.getNode();
        Box box = Box.newBuilder().setData(Any.newBuilder().setValue(ByteString.copyFrom(IntUtils.toBytes(node)))).build();
        ctx.writeAndFlush(box);
        logger.info("Register node '{}' to leader node {}", node, ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.debug("Leader node disconnected {}", ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("An error occurred", cause);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        if (!(message instanceof Packet packet)) {
            logger.warn("Invalid message {}\n{}", ctx.channel(), message);
            return;
        }
        contextManager.forward(packet);
    }
}

