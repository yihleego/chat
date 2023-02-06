package io.leego.chat.server.context;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import io.leego.chat.constant.AttributeKeys;
import io.leego.chat.constant.Messages;
import io.leego.chat.core.Box;
import io.leego.chat.core.Packet;
import io.leego.chat.manager.GroupManager;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * @author Leego Yih
 */
public abstract class AbstractContextManager implements ContextManager {
    protected final ExecutorService executorService;
    protected final GroupManager groupManager;

    protected AbstractContextManager(int threads, GroupManager groupManager) {
        int n = threads > 0 ? threads : Runtime.getRuntime().availableProcessors() * 2;
        this.executorService = Executors.newFixedThreadPool(Math.max(1, n));
        this.groupManager = groupManager;
    }

    @Override
    public void send(ChannelHandlerContext ctx, int code, Message.Builder data) {
        ctx.writeAndFlush(toBox(code, data));
    }

    @Override
    public void sendToUser(Long userId, int code, Message.Builder data) {
        // Send without callback
        sendToUser(userId, code, data, null);
    }

    @Override
    public void sendToGroup(Long sender, Long groupId, int code, Message.Builder data) {
        // Send without callback
        sendToGroup(sender, groupId, code, data, null);
    }

    protected Packet toPacket(Long target, Box box) {
        return Packet.newBuilder().addTarget(target).setBox(box).build();
    }

    protected Packet toPacket(List<Long> targets, Box box) {
        return Packet.newBuilder().addAllTarget(targets).setBox(box).build();
    }

    protected Packet toPacket(Long target, Integer client, Box box) {
        return Packet.newBuilder().addTarget(target).addClient(client).setBox(box).build();
    }

    protected Packet toPacket(List<Long> targets, List<Integer> clients, Box box) {
        return Packet.newBuilder().addAllTarget(targets).addAllClient(clients).setBox(box).build();
    }

    protected Box toBox(int code, Message.Builder data) {
        return Box.newBuilder()
                .setCode(code)
                .setData(Any.newBuilder().setValue(data.build().toByteString()))
                .build();
    }

    protected Box toBox(int code, String data) {
        return Box.newBuilder()
                .setCode(code)
                .setData(Any.newBuilder().setValue(ByteString.copyFrom(data, StandardCharsets.UTF_8)))
                .build();
    }

    protected String toString(Any any) {
        if (any == null) {
            return null;
        }
        return any.getValue().toString(StandardCharsets.UTF_8);
    }

    protected void kickOut(ChannelHandlerContext ctx) {
        ctx.channel().attr(AttributeKeys.KICKED_OUT).set(true);
        ctx.writeAndFlush(Messages.KICKED_OUT);
        ctx.close();
    }

    protected boolean isKickedOut(ChannelHandlerContext ctx) {
        return ctx.channel().hasAttr(AttributeKeys.KICKED_OUT);
    }

    protected Long getUserId(ChannelHandlerContext ctx) {
        return ctx.channel().attr(AttributeKeys.USER_ID).get();
    }

    protected Integer getClient(ChannelHandlerContext ctx) {
        return ctx.channel().attr(AttributeKeys.CLIENT).get();
    }

    protected int getChannelId(ChannelHandlerContext ctx) {
        return ctx.channel().id().hashCode();
    }

    protected void accept(Consumer<Integer> callback, int n) {
        if (callback != null) {
            callback.accept(n);
        }
    }
}
