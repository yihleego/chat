package io.leego.chat.handler.codec;

import io.leego.chat.util.ChatFactory;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

/**
 * @author Yihleego
 */
@ChannelHandler.Sharable
public class WebSocketFrameBoxEncoder extends MessageToMessageEncoder<ChatFactory.Box> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ChatFactory.Box msg, List<Object> out) {
        out.add(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(msg.toByteArray())));
    }

}
