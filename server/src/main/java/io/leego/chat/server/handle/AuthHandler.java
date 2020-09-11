package io.leego.chat.server.handle;

import io.leego.chat.MockedSessions;
import io.leego.chat.enums.Code;
import io.leego.chat.security.SecurityKey;
import io.leego.chat.security.UserDetail;
import io.leego.chat.util.AttrKey;
import io.leego.chat.util.ChatUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yihleego
 */
@ChannelHandler.Sharable
public class AuthHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(AuthHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.debug("Client is authenticating {}({})", ctx.channel().id(), ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.debug("Client is not authenticated {}({})", ctx.channel().id(), ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("An error occurred while authenticating {}({})", ctx.channel().id(), ctx.channel().remoteAddress(), cause);
        sendUnauthenticatedAndClose(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        if (!(message instanceof HttpRequest)) {
            logger.warn("Received non-authentication message from client {}({})", ctx.channel().id(), ctx.channel().remoteAddress());
            sendUnauthenticatedAndClose(ctx);
            return;
        }
        String token = getParamToken((HttpRequest) message);
        if (!isAuthenticated(ctx, token)) {
            sendUnauthenticatedAndClose(ctx);
            return;
        }
        ctx.pipeline().remove(this.getClass());
        ctx.fireChannelRead(message);
        ctx.fireChannelActive();
    }

    private String getParamToken(HttpRequest request) {
        String uri = request.uri();
        if (uri == null || uri.indexOf('?') < 0) {
            return null;
        }
        String paramString = uri.substring(uri.lastIndexOf('?') + 1);
        if (paramString.length() == 0) {
            return null;
        }
        String[] params = paramString.split("&");
        for (String param : params) {
            String[] kv = param.split("=");
            if (kv.length == 1) {
                if (kv[0].equals(SecurityKey.ACCESS_TOKEN)) {
                    return null;
                }
            } else if (kv.length == 2) {
                if (kv[0].equals(SecurityKey.ACCESS_TOKEN)) {
                    return kv[1];
                }
            }
        }
        return null;
    }

    private boolean isAuthenticated(ChannelHandlerContext ctx, String token) {
        if (token == null) {
            return false;
        }
        UserDetail user = MockedSessions.getByToken(token);
        if (user == null) {
            return false;
        }
        ctx.channel().attr(AttrKey.ATTR_USER).set(user);
        ctx.channel().attr(AttrKey.ATTR_USER_ID).set(user.getId());
        return true;
    }

    private void sendUnauthenticatedAndClose(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(ChatUtils.boxed(Code.UNAUTHENTICATED.getCode()));
        ctx.close();
    }

}
