package io.leego.chat.server.handle;

import io.leego.chat.enums.Code;
import io.leego.chat.util.ChatUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author Yihleego
 */
@ChannelHandler.Sharable
public class AuthTimeoutHandler extends IdleStateHandler {
    private static final Logger logger = LoggerFactory.getLogger(AuthTimeoutHandler.class);

    public AuthTimeoutHandler(long timeout, TimeUnit unit) {
        super(timeout, 0L, 0L, unit);
    }

    @Override
    protected final void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) {
        if (evt.state() == IdleState.READER_IDLE) {
            logger.warn("Client authentication timeout {}({})", ctx.channel().id(), ctx.channel().remoteAddress());
            ctx.writeAndFlush(ChatUtils.newBox(Code.UNAUTHENTICATED.getCode()));
            ctx.close();
        }
    }

}
