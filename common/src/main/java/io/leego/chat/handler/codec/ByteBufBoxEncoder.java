package io.leego.chat.handler.codec;

import io.leego.chat.util.ChatFactory;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author Yihleego
 */
@ChannelHandler.Sharable
public class ByteBufBoxEncoder extends MessageToMessageEncoder<ChatFactory.Box> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ChatFactory.Box msg, List<Object> out) {
        out.add(Unpooled.wrappedBuffer(msg.toByteArray()));
    }

}
