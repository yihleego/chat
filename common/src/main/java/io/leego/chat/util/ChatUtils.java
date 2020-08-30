package io.leego.chat.util;

/**
 * @author Yihleego
 */
public final class ChatUtils {

    public static ChatFactory.Box newBox(int code) {
        return ChatFactory.Box.newBuilder()
                .setCode(code)
                .build();
    }

}
