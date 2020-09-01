package io.leego.chat.handler.codec;

import com.google.protobuf.InvalidProtocolBufferException;
import io.leego.chat.util.ChatFactory;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;

/**
 * @author Yihleego
 */
@ChannelHandler.Sharable
public class WebSocketFrameBoxDecoder extends MessageToMessageDecoder<WebSocketFrame> {

    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) throws InvalidProtocolBufferException {
        out.add(ChatFactory.Box.parseFrom(ByteBufUtil.getBytes(msg.content())));
    }

}