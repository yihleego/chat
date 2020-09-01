package io.leego.chat.server.handle;

import com.google.protobuf.InvalidProtocolBufferException;
import io.leego.chat.MockedSessions;
import io.leego.chat.SecurityKey;
import io.leego.chat.UserDetail;
import io.leego.chat.enums.Code;
import io.leego.chat.util.AttrKey;
import io.leego.chat.util.ChatFactory;
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
        logger.error("An error occurred when the client is authenticating {}({})", ctx.channel().id(), ctx.channel().remoteAddress(), cause);
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        if (message instanceof ChatFactory.Box) {
            try {
                ChatFactory.Box box = (ChatFactory.Box) message;
                if (box.getCode() != Code.AUTHENTICATION.getCode()) {
                    sendUnauthenticatedAndClose(ctx);
                    return;
                }
                String token = box.getData().unpack(ChatFactory.Token.class).getValue();
                if (!isAuthenticated(ctx, token)) {
                    sendUnauthenticatedAndClose(ctx);
                    return;
                }
                ctx.pipeline().remove(AuthTimeoutHandler.class);
                ctx.pipeline().remove(this.getClass());
                ctx.fireChannelActive();
                sendAuthenticated(ctx);
            } catch (InvalidProtocolBufferException e) {
                logger.error("Failed to parse message", e);
                sendUnauthenticatedAndClose(ctx);
            }
        } else if (message instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) message;
            String token = getParamToken(request.uri());
            if (!isAuthenticated(ctx, token)) {
                sendUnauthenticatedAndClose(ctx);
                return;
            }
            ctx.pipeline().remove(this.getClass());
            ctx.fireChannelRead(message);
            ctx.fireChannelActive();
        } else {
            logger.warn("Received non-authentication message from client {}({})", ctx.channel().id(), ctx.channel().remoteAddress());
            sendUnauthenticatedAndClose(ctx);
        }
    }

    private String getParamToken(String uri) {
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
        ctx.writeAndFlush(ChatUtils.newBox(Code.UNAUTHENTICATED.getCode()));
        ctx.close();
    }

    private void sendAuthenticated(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(ChatUtils.newBox(Code.AUTHENTICATED.getCode()));
    }

}
