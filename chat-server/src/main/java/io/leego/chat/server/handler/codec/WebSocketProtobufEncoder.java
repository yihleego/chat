package io.leego.chat.server.handler.codec;

import com.google.protobuf.MessageLite;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

/**
 * @author Leego Yih
 */
@ChannelHandler.Sharable
public class WebSocketProtobufEncoder extends MessageToMessageEncoder<MessageLite> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MessageLite msg, List<Object> out) {
        if (msg != null) {
            out.add(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(msg.toByteArray())));
        }
    }
}
