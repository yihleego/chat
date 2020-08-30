package io.leego.chat.server.handle;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import io.leego.chat.constant.Constants;
import io.leego.chat.enums.Code;
import io.leego.chat.util.ByteBufUtils;
import io.leego.chat.util.ChatFactory;
import io.leego.chat.util.ChatUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Yihleego
 */
@ChannelHandler.Sharable
public class ChatServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ChatServerHandler.class);
    protected final ConcurrentMap<Long, ChannelHandlerContext> ctxMap = new ConcurrentHashMap<>(2 << 8);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (logger.isDebugEnabled()) {
            logger.debug("Client is connected {}({}) [id:{}]", ctx.channel().id(), ctx.channel().remoteAddress(), getUserId(ctx));
        }
        put(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (logger.isDebugEnabled()) {
            logger.debug("Client is disconnected {}({}) [id:{}]", ctx.channel().id(), ctx.channel().remoteAddress(), getUserId(ctx));
        }
        remove(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("An error occurred on the client {}({}) [id:{}]", ctx.channel().id(), ctx.channel().remoteAddress(), getUserId(ctx), cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
            logger.warn("Client will be disconnected because no data was either received or sent for a long time {}({}) [id:{}]",
                    ctx.channel().id(), ctx.channel().remoteAddress(), getUserId(ctx));
            close(ctx);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        if (!(message instanceof ByteBuf)) {
            logger.warn("Received empty message from client {}({}) [id:{}]", ctx.channel().id(), ctx.channel().remoteAddress(), getUserId(ctx));
            return;
        }
        try {
            ChatFactory.Box box = ChatFactory.Box.parseFrom(ByteBufUtils.toBytes((ByteBuf) message));
            process(ctx, box);
        } catch (Exception e) {
            logger.error("Failed to parse message", e);
        } finally {
            ReferenceCountUtil.release(message);
        }
    }

    protected void process(ChannelHandlerContext ctx, ChatFactory.Box box) {
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
                logger.warn("Received unknown message type from client {}({}) [id:{}]", ctx.channel().id(), ctx.channel().remoteAddress(), getUserId(ctx));
                break;
        }
    }

    protected void heartbeat(ChannelHandlerContext ctx) {
        if (logger.isDebugEnabled()) {
            logger.debug("Received heartbeat message type from client {}({}) [id:{}]", ctx.channel().id(), ctx.channel().remoteAddress(), getUserId(ctx));
        }
        send(ctx, ChatUtils.newBox(Code.HEARTBEAT.getCode()));
    }

    protected void sendMessage(ChannelHandlerContext ctx, ChatFactory.Box box) {
        ChatFactory.Message message = unpack(box.getData(), ChatFactory.Message.class, ctx);
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
        ChatFactory.Message message = unpack(box.getData(), ChatFactory.Message.class, ctx);
        if (message == null) {
            return;
        }
        send(message.getSender(), Code.DELIVERED_MESSAGE, message);
    }

    protected <T extends com.google.protobuf.Message> T unpack(Any any, Class<T> clazz, ChannelHandlerContext ctx) {
        if (any == null) {
            logger.error("Unpacking object cannot be null");
            close(ctx);
            return null;
        }
        try {
            return any.unpack(clazz);
        } catch (InvalidProtocolBufferException e) {
            logger.error("", e);
        }
        close(ctx);
        return null;
    }

    protected void send(ChannelHandlerContext ctx, Code code, com.google.protobuf.Message data) {
        send(ctx, ChatFactory.Box.newBuilder()
                .setCode(code.getCode())
                .setData(Any.pack(data))
                .build()
                .toByteArray());
    }

    protected void send(ChannelHandlerContext ctx, ChatFactory.Box box) {
        send(ctx, box.toByteArray());
    }

    protected void send(ChannelHandlerContext ctx, byte[] data) {
        ctx.writeAndFlush(Unpooled.wrappedBuffer(data));
    }

    protected boolean send(Long target, Code code, com.google.protobuf.Message data) {
        return send(target, ChatFactory.Box.newBuilder()
                .setCode(code.getCode())
                .setData(Any.pack(data))
                .build()
                .toByteArray());
    }

    protected boolean send(Long target, ChatFactory.Box box) {
        return send(target, box.toByteArray());
    }

    protected boolean send(Long target, byte[] data) {
        ChannelHandlerContext ctx = ctxMap.get(target);
        if (ctx != null) {
            ctx.writeAndFlush(Unpooled.wrappedBuffer(data));
            return true;
        } else {
            return sendToOffline(target, data);
        }
    }

    protected boolean sendToOffline(Long target, byte[] data) {
        // ignored
        return false;
    }

    protected void close(ChannelHandlerContext ctx) {
        remove(ctx);
        ctx.close();
    }

    protected ChannelHandlerContext remove(ChannelHandlerContext ctx) {
        for (Map.Entry<Long, ChannelHandlerContext> entry : ctxMap.entrySet()) {
            Long k = entry.getKey();
            ChannelHandlerContext v = entry.getValue();
            if (v == ctx) {
                return ctxMap.remove(k);
            }
        }
        return null;
    }

    protected void put(ChannelHandlerContext ctx) {
        ctxMap.putIfAbsent(getUserId(ctx), ctx);
    }

    protected Long getUserId(ChannelHandlerContext ctx) {
        return (Long) ctx.channel().attr(Constants.ATTR_USER_ID).get();
    }

}

