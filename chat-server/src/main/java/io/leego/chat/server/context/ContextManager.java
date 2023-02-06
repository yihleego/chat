package io.leego.chat.server.context;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;

import java.util.function.Consumer;

/**
 * @author Leego Yih
 */
public interface ContextManager {

    /**
     * Sets the context.
     *
     * @param ctx the context.
     */
    void set(ChannelHandlerContext ctx);

    /**
     * Removes the context.
     *
     * @param ctx the context.
     */
    void remove(ChannelHandlerContext ctx);

    /**
     * Returns the number of all contexts.
     *
     * @return the number of all contexts.
     */
    int size();

    /**
     * Syncs the message with the given {@literal userId}, excluding the specified context.
     *
     * @param ctx  the context.
     * @param code the message code.
     * @param data the message data.
     */
    void sync(ChannelHandlerContext ctx, int code, Message.Builder data);

    /**
     * Sends the message with the given context.
     *
     * @param ctx  the context.
     * @param code the message code.
     * @param data the message data.
     */
    void send(ChannelHandlerContext ctx, int code, Message.Builder data);

    /**
     * Sends the message with the given {@literal userId}.
     *
     * @param userId the user id.
     * @param code   the message code.
     * @param data   the message data.
     */
    void sendToUser(Long userId, int code, Message.Builder data);

    /**
     * Sends the message with the given {@literal userId}.
     *
     * @param userId   the user id.
     * @param code     the message code.
     * @param data     the message data.
     * @param callback the callback with the number of online clients for the user, could be {@literal null}.
     */
    void sendToUser(Long userId, int code, Message.Builder data, Consumer<Integer> callback);

    /**
     * Sends the message to group members with the given {@literal groupId}.
     *
     * @param sender  the user id of sender.
     * @param groupId the group id.
     * @param code    the message code.
     * @param data    the message data.
     */
    void sendToGroup(Long sender, Long groupId, int code, Message.Builder data);

    /**
     * Sends the message to group members with the given {@literal groupId}.
     *
     * @param sender   the user id of sender.
     * @param groupId  the group id.
     * @param code     the message code.
     * @param data     the message data.
     * @param callback the callback with the number of online clients for the user, could be {@literal null}.
     */
    void sendToGroup(Long sender, Long groupId, int code, Message.Builder data, Consumer<Integer> callback);

}
