package io.leego.chat.cluster.handler;

import io.leego.chat.cluster.RegisteredEvent;
import io.leego.chat.cluster.context.ClusterContextManager;
import io.leego.chat.core.Box;
import io.leego.chat.util.IntUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Leego Yih
 */
@ChannelHandler.Sharable
public class ClusterDiscoveryServerHandler extends ChannelInboundHandlerAdapter implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(ClusterDiscoveryServerHandler.class);
    private final ClusterContextManager contextManager;
    private ApplicationContext applicationContext;

    public ClusterDiscoveryServerHandler(ClusterContextManager contextManager) {
        this.contextManager = contextManager;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.debug("Follower node connected {}", ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.debug("Follower node disconnected {}", ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            logger.warn("Follower node is about to disconnect, because it has not been registered {}", ctx.channel());
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("An error occurred", cause);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        if (!(message instanceof Box box)) {
            logger.warn("Invalid message '{}' {}", message, ctx.channel());
            return;
        }
        byte[] bytes = box.getData().getValue().toByteArray();
        if (bytes == null || bytes.length != 4) {
            logger.error("Invalid node '{}' {}", bytes, ctx.channel());
            return;
        }
        int node = IntUtils.toInt(bytes);
        ctx.channel().pipeline().remove(IdleStateHandler.class);
        logger.info("Follower node '{}' registered {}", node, ctx.channel());
        contextManager.register(ctx, node);
        ctx.channel().closeFuture().addListener((listener) -> {
            logger.info("Follower node '{}' unregistered {}", node, ctx.channel());
            contextManager.unregister(ctx, node);
        });
        // Try to establish a reverse connection to the follower node
        applicationContext.publishEvent(new RegisteredEvent(node));
    }
}
