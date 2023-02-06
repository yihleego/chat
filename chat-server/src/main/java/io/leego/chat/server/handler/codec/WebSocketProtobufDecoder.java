package io.leego.chat.server.handler.codec;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.internal.ObjectUtil;

import java.util.List;

/**
 * @author Leego Yih
 */
@ChannelHandler.Sharable
public class WebSocketProtobufDecoder extends MessageToMessageDecoder<WebSocketFrame> {
    private final MessageLite prototype;
    private final ExtensionRegistryLite extensionRegistry;

    public WebSocketProtobufDecoder(MessageLite prototype) {
        this(prototype, null);
    }

    public WebSocketProtobufDecoder(MessageLite prototype, ExtensionRegistry extensionRegistry) {
        this(prototype, (ExtensionRegistryLite) extensionRegistry);
    }

    public WebSocketProtobufDecoder(MessageLite prototype, ExtensionRegistryLite extensionRegistry) {
        this.prototype = ObjectUtil.checkNotNull(prototype, "prototype").getDefaultInstanceForType();
        this.extensionRegistry = extensionRegistry;
    }

    @Override
    protected void decode(ChannelHandlerContext context, WebSocketFrame msg, List<Object> out) throws Exception {
        ByteBuf buf = msg.content();
        final byte[] array;
        final int offset;
        final int length = buf.readableBytes();
        if (buf.hasArray()) {
            array = buf.array();
            offset = buf.arrayOffset() + buf.readerIndex();
        } else {
            array = ByteBufUtil.getBytes(buf, buf.readerIndex(), length, false);
            offset = 0;
        }
        if (extensionRegistry == null) {
            out.add(prototype.getParserForType().parseFrom(array, offset, length));
        } else {
            out.add(prototype.getParserForType().parseFrom(array, offset, length, extensionRegistry));
        }
    }
}
