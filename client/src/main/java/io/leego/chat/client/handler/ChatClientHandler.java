package io.leego.chat.client.handler;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import io.leego.chat.enums.Code;
import io.leego.chat.util.ChatFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yihleego
 */
@ChannelHandler.Sharable
public class ChatClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ChatClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.debug("Connected");
        ChatFactory.Token token = ChatFactory.Token.newBuilder()
                .setValue("dante")
                .build();
        ChatFactory.Box box = ChatFactory.Box.newBuilder()
                .setCode(Code.AUTHENTICATION.getCode())
                .setData(Any.pack(token))
                .build();
        ctx.writeAndFlush(box);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.debug("Disconnected");
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.debug("", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE) {
            logger.debug("Send heartbeat");
            send(ctx, Code.HEARTBEAT);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        if (!(message instanceof ChatFactory.Box)) {
            ReferenceCountUtil.release(message);
            logger.warn("Received empty message {}({})", ctx.channel().id(), ctx.channel().remoteAddress());
            return;
        }
        ChatFactory.Box box = (ChatFactory.Box) message;
        Code code = Code.getOrDefault(box.getCode(), Code.UNKNOWN);
        logger.debug("{}", code);
        switch (code) {
            case HEARTBEAT:
                heartbeat(ctx);
                break;
            case RECEIVE_MESSAGE:
                receiveMessage(ctx, box);
                break;
            case AUTHENTICATED:
                break;
            case UNAUTHENTICATED:
                break;
            default:
                logger.warn("Received unknown message {}({})", ctx.channel().id(), ctx.channel().remoteAddress());
                break;
        }
    }

    private void heartbeat(ChannelHandlerContext ctx) {
        if (logger.isDebugEnabled()) {
            logger.debug("Received heartbeat {}({})", ctx.channel().id(), ctx.channel().remoteAddress());
        }
    }

    private void receiveMessage(ChannelHandlerContext ctx, ChatFactory.Box box) {
        ChatFactory.Message message = unpack(box.getData(), ChatFactory.Message.class);
        if (message == null) {
            return;
        }
        logger.debug("{} -> {}", message.getSender(), message.getRecipient());
        send(ctx, Code.RECEIVED_MESSAGE, message);
    }

    private <T extends com.google.protobuf.Message> T unpack(Any any, Class<T> clazz) {
        if (any == null) {
            return null;
        }
        try {
            return any.unpack(clazz);
        } catch (InvalidProtocolBufferException e) {
            logger.error("Failed to unpack", e);
        }
        return null;
    }

    private void send(ChannelHandlerContext ctx, Code code) {
        ctx.writeAndFlush(ChatFactory.Box.newBuilder()
                .setCode(code.getCode())
                .build());
    }

    private void send(ChannelHandlerContext ctx, Code code, com.google.protobuf.Message data) {
        ctx.writeAndFlush(ChatFactory.Box.newBuilder()
                .setCode(code.getCode())
                .setData(Any.pack(data))
                .build());
    }

}

