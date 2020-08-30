package io.leego.chat.util;

import io.netty.buffer.ByteBuf;

public final class ByteBufUtils {
    private ByteBufUtils() {
    }

    public static byte[] toBytes(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return bytes;
    }

}
