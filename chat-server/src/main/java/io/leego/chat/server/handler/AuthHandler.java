package io.leego.chat.server.handler;

import io.leego.chat.constant.AttributeKeys;
import io.leego.chat.constant.Codes;
import io.leego.chat.constant.Messages;
import io.leego.chat.core.Box;
import io.leego.security.Authentication;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author Leego Yih
 */
public class AuthHandler extends ReadTimeoutHandler {
    private static final Logger logger = LoggerFactory.getLogger(AuthHandler.class);
    private final Function<String, Authentication> security;

    public AuthHandler(long timeout, TimeUnit unit, Function<String, Authentication> security) {
        super(timeout, unit);
        this.security = security;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.debug("Client is waiting for authentication {}", ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.debug("Client ends authentication {}", ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("An error occurred during authentication {}", ctx.channel(), cause);
        accessDenied(ctx);
    }

    @Override
    protected void readTimedOut(ChannelHandlerContext ctx) {
        logger.warn("Client authentication timeout {}", ctx.channel());
        accessDenied(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        if (!(message instanceof Box box) || box.getCode() != Codes.AUTHENTICATION) {
            logger.warn("Non-authentication message {}", ctx.channel());
            accessDenied(ctx);
            return;
        }
        ctx.pipeline().remove(this);
        String token = box.getData().getValue().toString(StandardCharsets.UTF_8);
        Authentication a = security.apply(token);
        if (a == null) {
            logger.warn("Client not authenticated {} {}", token, ctx.channel());
            accessDenied(ctx);
            return;
        }
        logger.debug("Client authenticated {}", ctx.channel());
        ctx.channel().attr(AttributeKeys.USER_ID).set(a.userId());
        ctx.channel().attr(AttributeKeys.CLIENT).set(a.clientType() != null ? (int) a.clientType() : 0);
        ctx.fireChannelActive();
        accessGranted(ctx);
    }

    protected void accessGranted(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Messages.AUTHENTICATED);
    }

    protected void accessDenied(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Messages.UNAUTHENTICATED);
        ctx.close();
    }
}
