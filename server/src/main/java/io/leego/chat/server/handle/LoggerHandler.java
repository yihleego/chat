package io.leego.chat.server.handle;

import io.leego.chat.constant.Constants;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yihleego
 */
@ChannelHandler.Sharable
public class LoggerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(LoggerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (logger.isDebugEnabled()) {
            logger.debug("Client is connected {}({}) [id:{}]",
                    ctx.channel().id(), ctx.channel().remoteAddress(), getUserId(ctx));
        }
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (logger.isDebugEnabled()) {
            logger.debug("Client is disconnected {}({}) [id:{}]",
                    ctx.channel().id(), ctx.channel().remoteAddress(), getUserId(ctx));
        }
        ctx.fireChannelInactive();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
            if (logger.isWarnEnabled()) {
                logger.warn("Client will be disconnected because no data was either received or sent for a long time {}({}) [id:{}]",
                        ctx.channel().id(), ctx.channel().remoteAddress(), getUserId(ctx));
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("An error occurred on the client {}({}) [id:{}]",
                ctx.channel().id(), ctx.channel().remoteAddress(), getUserId(ctx), cause);
        ctx.fireExceptionCaught(cause);
    }

    protected Long getUserId(ChannelHandlerContext ctx) {
        return (Long) ctx.channel().attr(Constants.ATTR_USER_ID).get();
    }

}
