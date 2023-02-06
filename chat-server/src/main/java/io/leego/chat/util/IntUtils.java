package io.leego.chat.util;

/**
 * @author Leego Yih
 */
public final class IntUtils {
    private IntUtils() {
    }

    public static byte[] toBytes(int i) {
        return new byte[]{(byte) (i >> 24), (byte) (i >> 16), (byte) (i >> 8), (byte) i};
    }

    public static int toInt(byte[] b) {
        return (b[0]) << 24 | (b[1] & 0xFF) << 16 | (b[2] & 0xFF) << 8 | (b[3] & 0xFF);
    }
}
