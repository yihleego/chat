package io.leego.chat.server.handler;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import io.leego.chat.constant.AttributeKeys;
import io.leego.chat.constant.Codes;
import io.leego.chat.constant.Messages;
import io.leego.chat.core.Box;
import io.leego.chat.core.BulkMessage;
import io.leego.chat.core.Message;
import io.leego.chat.server.context.ContextManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Leego Yih
 */
@ChannelHandler.Sharable
public class ChatServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ChatServerHandler.class);
    private final ContextManager contextManager;

    public ChatServerHandler(ContextManager contextManager) {
        this.contextManager = contextManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (logger.isDebugEnabled()) {
            logger.debug("Client connected {} [{}-{}]", ctx.channel(), getUserId(ctx), getClient(ctx));
        }
        contextManager.set(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (logger.isDebugEnabled()) {
            logger.debug("Client disconnected {} [{}-{}]", ctx.channel(), getUserId(ctx), getClient(ctx));
        }
        contextManager.remove(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent e) {
            logger.warn("Client is about to disconnect, because it is unresponsive for a long time {} {} [{}-{}]", e.state(), ctx.channel(), getUserId(ctx), getClient(ctx));
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("An error occurred {} [{}-{}]", ctx.channel(), getUserId(ctx), getClient(ctx), cause);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        if (!(message instanceof Box box)) {
            logger.error("Invalid message {} [{}-{}]\n{}", ctx.channel(), getUserId(ctx), getClient(ctx), message);
            return;
        }
        switch (box.getCode()) {
            case Codes.MESSAGE_SEND_NOTIFY -> sendMessage(ctx, box);
            case Codes.MESSAGE_TAKE_NOTIFY -> takeMessage(ctx, box);
            case Codes.MESSAGE_READ_NOTIFY -> readMessage(ctx, box);
            case Codes.MESSAGE_READ_BATCH_NOTIFY -> readMessageInBatch(ctx, box);
            case Codes.MESSAGE_REVOKE_NOTIFY -> revokeMessage(ctx, box);
            case Codes.MESSAGE_REMOVE_NOTIFY -> removeMessage(ctx, box);
            case Codes.GROUP_MESSAGE_SEND_NOTIFY -> sendGroupMessage(ctx, box);
            case Codes.GROUP_MESSAGE_TAKE_NOTIFY -> takeGroupMessage(ctx, box);
            case Codes.GROUP_MESSAGE_READ_NOTIFY -> readGroupMessage(ctx, box);
            case Codes.GROUP_MESSAGE_READ_BATCH_NOTIFY -> readGroupMessageInBatch(ctx, box);
            case Codes.GROUP_MESSAGE_REVOKE_NOTIFY -> revokeGroupMessage(ctx, box);
            case Codes.GROUP_MESSAGE_REMOVE_NOTIFY -> removeGroupMessage(ctx, box);
            case Codes.CONTACT_REQUEST_SEND_NOTIFY -> sendContactRequest(ctx, box);
            case Codes.CONTACT_REQUEST_TAKE_NOTIFY -> takeContactRequest(ctx, box);
            case Codes.CONTACT_EVENT_NOTIFY -> triggerContactEvent(ctx, box);
            case Codes.GROUP_EVENT_NOTIFY -> triggerGroupEvent(ctx, box);
            case Codes.GROUP_MEMBER_EVENT_NOTIFY -> triggerGroupMemberEvent(ctx, box);
            case Codes.HEARTBEAT -> heartbeat(ctx);
            default -> blackhole(ctx, box);
        }
    }

    private void blackhole(ChannelHandlerContext ctx, Box box) {
        logger.error("Invalid code {} [{}-{}]\n{}", ctx.channel(), getUserId(ctx), getClient(ctx), TextFormat.shortDebugString(box));
    }

    private void heartbeat(ChannelHandlerContext ctx) {
        if (logger.isDebugEnabled()) {
            logger.debug("[HEARTBEAT] {} [{}-{}]", ctx.channel(), getUserId(ctx), getClient(ctx));
        }
        ctx.writeAndFlush(Messages.HEARTBEAT);
    }

    private void sendMessage(ChannelHandlerContext ctx, Box box) throws InvalidProtocolBufferException {
        Message message = Message.parseFrom(box.getData().getValue());
        long id = message.getId();
        long recipient = message.getRecipient();
        if (!validate(id > 0 && recipient > 0, ctx, message, "MESSAGE_SEND")) {
            return;
        }
        Message.Builder elegant = Message.newBuilder().setId(id);
        // Sync to other clients, excluding self
        contextManager.sync(ctx, Codes.MESSAGE_SYNC_PUSH, elegant);
        // Send to all clients of the recipient
        contextManager.sendToUser(recipient, Codes.MESSAGE_SEND_PUSH, elegant, (count) -> {
            if (count > 0) return;
            contextManager.send(ctx, Codes.MESSAGE_TAKE_OFFLINE_PUSH, elegant);
            if (logger.isDebugEnabled()) {
                logger.debug("End sending message process [{}], no recipient online [{}] -> [{}]", id, getUserId(ctx), recipient);
            }
        });
    }

    private void takeMessage(ChannelHandlerContext ctx, Box box) throws InvalidProtocolBufferException {
        Message message = Message.parseFrom(box.getData().getValue());
        long id = message.getId();
        long sender = message.getSender();
        if (!validate(id > 0 && sender > 0, ctx, message, "MESSAGE_TAKE")) {
            return;
        }
        // Notify the sender that the message has been taken
        Message.Builder elegant = Message.newBuilder().setId(id);
        contextManager.sendToUser(sender, Codes.MESSAGE_TAKE_PUSH, elegant);
    }

    private void readMessage(ChannelHandlerContext ctx, Box box) throws InvalidProtocolBufferException {
        Message message = Message.parseFrom(box.getData().getValue());
        long id = message.getId();
        long sender = message.getSender();
        if (!validate(id > 0 && sender > 0, ctx, message, "MESSAGE_READ")) {
            return;
        }
        // Notify the sender that the message has been seen
        Message.Builder elegant = Message.newBuilder().setId(id);
        contextManager.sendToUser(sender, Codes.MESSAGE_READ_PUSH, elegant);
    }

    private void readMessageInBatch(ChannelHandlerContext ctx, Box box) throws InvalidProtocolBufferException {
        BulkMessage message = BulkMessage.parseFrom(box.getData().getValue());
        List<Long> ids = message.getIdList();
        long sender = message.getSender();
        if (!validate(ids.size() > 0 && sender > 0, ctx, message, "MESSAGE_READ_BATCH")) {
            return;
        }
        // Notify the sender that the message has been seen
        BulkMessage.Builder elegant = message.toBuilder().clearSender().clearRecipient();
        contextManager.sendToUser(sender, Codes.MESSAGE_READ_BATCH_PUSH, elegant);
    }

    private void revokeMessage(ChannelHandlerContext ctx, Box box) throws InvalidProtocolBufferException {
        Message message = Message.parseFrom(box.getData().getValue());
        long id = message.getId();
        long recipient = message.getRecipient();
        if (!validate(id > 0 && recipient > 0, ctx, message, "MESSAGE_REVOKE")) {
            return;
        }
        Message.Builder elegant = Message.newBuilder().setId(id);
        // Sync to other clients of the sender, excluding self
        contextManager.sync(ctx, Codes.MESSAGE_SYNC_PUSH, elegant);
        // Send to all clients of the recipient
        contextManager.sendToUser(recipient, Codes.MESSAGE_REVOKE_PUSH, elegant, (count) -> {
            if (count > 0) return;
            contextManager.send(ctx, Codes.MESSAGE_REMOVE_OFFLINE_PUSH, elegant);
            if (logger.isDebugEnabled()) {
                logger.debug("End revoking message process [{}], no recipient online [{}] -> [{}]", id, getUserId(ctx), recipient);
            }
        });
    }

    private void removeMessage(ChannelHandlerContext ctx, Box box) throws InvalidProtocolBufferException {
        Message message = Message.parseFrom(box.getData().getValue());
        long id = message.getId();
        long sender = message.getSender();
        if (!validate(id > 0 && sender > 0, ctx, message, "MESSAGE_REMOVE")) {
            return;
        }
        // Notify the sender that the message has been revoked
        Message.Builder elegant = Message.newBuilder().setId(id);
        contextManager.sendToUser(sender, Codes.MESSAGE_REMOVE_PUSH, elegant);
    }

    private void sendGroupMessage(ChannelHandlerContext ctx, Box box) throws InvalidProtocolBufferException {
        Message message = Message.parseFrom(box.getData().getValue());
        long id = message.getId();
        long group = message.getRecipient();
        if (!validate(id > 0 && group > 0, ctx, message, "GROUP_MESSAGE_SEND")) {
            return;
        }
        Long sender = getUserId(ctx);
        Message.Builder elegant = Message.newBuilder().setId(id);
        // Sync to other clients, excluding self
        contextManager.sync(ctx, Codes.GROUP_MESSAGE_SYNC_PUSH, elegant);
        // Send to all clients of the members
        contextManager.sendToGroup(sender, group, Codes.GROUP_MESSAGE_SEND_PUSH, elegant, (count) -> {
            if (count > 0) return;
            contextManager.send(ctx, Codes.GROUP_MESSAGE_TAKE_OFFLINE_PUSH, elegant);
            logger.debug("End sending group message process [{}], no recipient online [{}] -> [{}]", id, sender, group);
        });
    }

    private void takeGroupMessage(ChannelHandlerContext ctx, Box box) throws InvalidProtocolBufferException {
        Message message = Message.parseFrom(box.getData().getValue());
        long id = message.getId();
        long sender = message.getSender();
        if (!validate(id > 0 && sender > 0, ctx, message, "GROUP_MESSAGE_TAKE")) {
            return;
        }
        // Notify the sender that the message has been taken
        Message.Builder elegant = Message.newBuilder().setId(id);
        contextManager.sendToUser(sender, Codes.GROUP_MESSAGE_TAKE_PUSH, elegant);
    }

    private void readGroupMessage(ChannelHandlerContext ctx, Box box) throws InvalidProtocolBufferException {
        Message message = Message.parseFrom(box.getData().getValue());
        long id = message.getId();
        long sender = message.getSender();
        if (!validate(id > 0 && sender > 0, ctx, message, "GROUP_MESSAGE_READ")) {
            return;
        }
        // Notify the sender that the message has been seen
        Message.Builder elegant = Message.newBuilder().setId(id);
        contextManager.sendToUser(sender, Codes.GROUP_MESSAGE_READ_PUSH, elegant);
    }

    private void readGroupMessageInBatch(ChannelHandlerContext ctx, Box box) throws InvalidProtocolBufferException {
        BulkMessage message = BulkMessage.parseFrom(box.getData().getValue());
        List<Long> ids = message.getIdList();
        long sender = message.getSender();
        if (!validate(ids.size() > 0 && sender > 0, ctx, message, "GROUP_MESSAGE_READ_BATCH")) {
            return;
        }
        // Notify the sender that the message has been seen
        BulkMessage.Builder elegant = message.toBuilder().clearSender().clearRecipient();
        contextManager.sendToUser(sender, Codes.GROUP_MESSAGE_READ_BATCH_PUSH, elegant);
    }

    private void revokeGroupMessage(ChannelHandlerContext ctx, Box box) throws InvalidProtocolBufferException {
        Message message = Message.parseFrom(box.getData().getValue());
        long id = message.getId();
        long group = message.getRecipient();
        if (!validate(id > 0 && group > 0, ctx, message, "GROUP_MESSAGE_REVOKE")) {
            return;
        }
        Long sender = getUserId(ctx);
        Message.Builder elegant = Message.newBuilder().setId(id);
        // Sync to other clients of the sender, excluding self
        contextManager.sync(ctx, Codes.GROUP_MESSAGE_SYNC_PUSH, elegant);
        // Send to all clients of the members
        contextManager.sendToGroup(sender, group, Codes.GROUP_MESSAGE_REVOKE_PUSH, elegant, (count) -> {
            if (count > 0) return;
            contextManager.send(ctx, Codes.GROUP_MESSAGE_REMOVE_OFFLINE_PUSH, elegant);
            logger.debug("End revoking group message process [{}], no recipient online [{}] -> [{}]", id, sender, group);
        });
    }

    private void removeGroupMessage(ChannelHandlerContext ctx, Box box) throws InvalidProtocolBufferException {
        Message message = Message.parseFrom(box.getData().getValue());
        long id = message.getId();
        long sender = message.getSender();
        if (!validate(id > 0 && sender > 0, ctx, message, "GROUP_MESSAGE_REMOVE")) {
            return;
        }
        // Notify the sender that the message has been revoked
        Message.Builder elegant = Message.newBuilder().setId(id);
        contextManager.sendToUser(sender, Codes.GROUP_MESSAGE_REMOVE_PUSH, elegant);
    }

    private void sendContactRequest(ChannelHandlerContext ctx, Box box) throws InvalidProtocolBufferException {
        Message message = Message.parseFrom(box.getData().getValue());
        long id = message.getId();
        long recipient = message.getRecipient();
        if (!validate(id > 0 && recipient > 0, ctx, message, "CONTACT_REQUEST_SEND")) {
            return;
        }
        Long sender = getUserId(ctx);
        // Send to all clients of the recipient
        Message.Builder elegant = Message.newBuilder().setId(id);
        contextManager.sendToUser(recipient, Codes.CONTACT_REQUEST_SEND_PUSH, elegant, (count) -> {
            if (count > 0) return;
            contextManager.send(ctx, Codes.CONTACT_REQUEST_TAKE_OFFLINE_PUSH, elegant);
            logger.debug("End sending contact request process [{}], no recipient online [{}] -> [{}]", id, sender, recipient);
        });
    }

    private void takeContactRequest(ChannelHandlerContext ctx, Box box) throws InvalidProtocolBufferException {
        Message message = Message.parseFrom(box.getData().getValue());
        long id = message.getId();
        long sender = message.getSender();
        if (!validate(id > 0 && sender > 0, ctx, message, "CONTACT_REQUEST_TAKE")) {
            return;
        }
        // Notify the sender that the message has been taken
        Message.Builder elegant = Message.newBuilder().setId(id);
        contextManager.sendToUser(sender, Codes.CONTACT_REQUEST_TAKE_PUSH, elegant);
    }

    private void triggerContactEvent(ChannelHandlerContext ctx, Box box) throws InvalidProtocolBufferException {
        Message message = Message.parseFrom(box.getData().getValue());
        long id = message.getId();
        long recipient = message.getRecipient();
        if (!validate(id > 0 && recipient > 0, ctx, message, "CONTACT_EVENT")) {
            return;
        }
        // Send to all clients of the recipient
        Message.Builder elegant = Message.newBuilder().setId(id);
        contextManager.sendToUser(recipient, Codes.CONTACT_EVENT_PUSH, elegant);
    }

    private void triggerGroupEvent(ChannelHandlerContext ctx, Box box) throws InvalidProtocolBufferException {
        Message message = Message.parseFrom(box.getData().getValue());
        long id = message.getId();
        long group = message.getRecipient();
        if (!validate(id > 0 && group > 0, ctx, message, "GROUP_EVENT")) {
            return;
        }
        Long sender = getUserId(ctx);
        // Send to all clients of the members
        Message.Builder elegant = Message.newBuilder().setId(id);
        contextManager.sendToGroup(sender, group, Codes.GROUP_EVENT_PUSH, elegant);
    }

    private void triggerGroupMemberEvent(ChannelHandlerContext ctx, Box box) throws InvalidProtocolBufferException {
        Message message = Message.parseFrom(box.getData().getValue());
        long id = message.getId();
        long group = message.getRecipient();
        if (!validate(id > 0 && group > 0, ctx, message, "GROUP_MEMBER_EVENT")) {
            return;
        }
        Long sender = getUserId(ctx);
        // Send to all clients of the members
        Message.Builder elegant = Message.newBuilder().setId(id);
        contextManager.sendToGroup(sender, group, Codes.GROUP_MEMBER_EVENT_PUSH, elegant);
    }

    private boolean validate(boolean condition, ChannelHandlerContext ctx, MessageOrBuilder message, String title) {
        if (condition) {
            if (logger.isDebugEnabled()) {
                logger.debug("[{}] [{}] {} [{}-{}]", title, TextFormat.shortDebugString(message), ctx.channel(), getUserId(ctx), getClient(ctx));
            }
            return true;
        }
        logger.error("[{}] Invalid message: [{}] {} [{}-{}]", title, TextFormat.shortDebugString(message), ctx.channel(), getUserId(ctx), getClient(ctx));
        return false;
    }

    private Long getUserId(ChannelHandlerContext ctx) {
        return ctx.channel().attr(AttributeKeys.USER_ID).get();
    }

    private Integer getClient(ChannelHandlerContext ctx) {
        return ctx.channel().attr(AttributeKeys.CLIENT).get();
    }
}

