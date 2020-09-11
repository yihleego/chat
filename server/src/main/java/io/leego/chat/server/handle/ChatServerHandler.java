package io.leego.chat.server.handle;

import com.google.protobuf.InvalidProtocolBufferException;
import io.leego.chat.enums.Code;
import io.leego.chat.util.AttrKey;
import io.leego.chat.util.ChatFactory;
import io.leego.chat.util.ChatUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Yihleego
 */
@ChannelHandler.Sharable
public class ChatServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ChatServerHandler.class);
    protected final ConcurrentMap<Long, ChannelHandlerContext> contexts = new ConcurrentHashMap<>(1 << 10);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        put(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        remove(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
            close(ctx);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        if (!(message instanceof ChatFactory.Box)) {
            ReferenceCountUtil.release(message);
            logger.warn("Received empty message from client {}({}) [id:{}]", ctx.channel().id(), ctx.channel().remoteAddress(), getUserId(ctx));
            return;
        }
        ChatFactory.Box box = (ChatFactory.Box) message;
        Code code = Code.getOrDefault(box.getCode(), Code.UNKNOWN);
        switch (code) {
            case HEARTBEAT:
                heartbeat(ctx);
                break;
            case SEND_MESSAGE:
                sendMessage(ctx, box);
                break;
            case RECEIVED_MESSAGE:
                receivedMessage(ctx, box);
                break;
            default:
                logger.warn("Received unknown message from client {}({}) [id:{}]", ctx.channel().id(), ctx.channel().remoteAddress(), getUserId(ctx));
                break;
        }
    }

    protected void heartbeat(ChannelHandlerContext ctx) {
        if (logger.isDebugEnabled()) {
            logger.debug("Received heartbeat from client {}({}) [id:{}]", ctx.channel().id(), ctx.channel().remoteAddress(), getUserId(ctx));
        }
        send(ctx, Code.HEARTBEAT);
    }

    protected void sendMessage(ChannelHandlerContext ctx, ChatFactory.Box box) {
        ChatFactory.Message message = getMessage(box);
        if (message == null) {
            return;
        }
        if (!Objects.equals(message.getSender(), getUserId(ctx))) {
            logger.error("Received suspicious message from client {}({}) [id:{}]\n{}", ctx.channel().id(), ctx.channel().remoteAddress(), getUserId(ctx), message);
            close(ctx);
            return;
        }
        boolean success = send(message.getRecipient(), Code.RECEIVE_MESSAGE, message);
        if (!success) {
            send(ctx, Code.DELIVERED_OFFLINE_MESSAGE, message);
        }
    }

    protected void receivedMessage(ChannelHandlerContext ctx, ChatFactory.Box box) {
        ChatFactory.Message message = getMessage(box);
        if (message == null) {
            return;
        }
        send(message.getSender(), Code.DELIVERED_MESSAGE, message);
    }

    protected ChatFactory.Message getMessage(ChatFactory.Box box) {
        try {
            return ChatFactory.Message.parseFrom(box.getData().getValue());
        } catch (InvalidProtocolBufferException e) {
            logger.error("Failed to parse", e);
        }
        return null;
    }

    protected void send(ChannelHandlerContext ctx, Code code) {
        ctx.writeAndFlush(ChatUtils.boxed(code));
    }

    protected void send(ChannelHandlerContext ctx, Code code, com.google.protobuf.Message data) {
        ctx.writeAndFlush(ChatUtils.boxed(code, data));
    }

    protected boolean send(Long target, Code code) {
        return send(target, ChatUtils.boxed(code));
    }

    protected boolean send(Long target, Code code, com.google.protobuf.Message data) {
        return send(target, ChatUtils.boxed(code, data));
    }

    protected boolean send(Long target, ChatFactory.Box box) {
        ChannelHandlerContext ctx = contexts.get(target);
        if (ctx == null) {
            return false;
        }
        ctx.writeAndFlush(box);
        return true;
    }

    protected void close(ChannelHandlerContext ctx) {
        ctx.close();
    }

    protected ChannelHandlerContext remove(ChannelHandlerContext ctx) {
        Boolean kicked = ctx.channel().attr(AttrKey.ATTR_KICKED_OUT).get();
        if (kicked == null || !kicked) {
            return contexts.remove(getUserId(ctx));
        }
        return null;
    }

    protected void put(ChannelHandlerContext ctx) {
        ChannelHandlerContext old = contexts.put(getUserId(ctx), ctx);
        if (old != null && old != ctx) {
            send(old, Code.KICKED_OUT);
            old.channel().attr(AttrKey.ATTR_KICKED_OUT).set(true);
            old.close();
        }
    }

    protected Long getUserId(ChannelHandlerContext ctx) {
        return ctx.channel().attr(AttrKey.ATTR_USER_ID).get();
    }

}

