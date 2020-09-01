package io.leego.chat.handler.codec;

import com.google.protobuf.InvalidProtocolBufferException;
import io.leego.chat.util.ChatFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @author Yihleego
 */
@ChannelHandler.Sharable
public class ByteBufBoxDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws InvalidProtocolBufferException {
        out.add(ChatFactory.Box.parseFrom(ByteBufUtil.getBytes(msg)));
    }

}