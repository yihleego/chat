package io.leego.chat.util;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import io.leego.chat.enums.Code;

/**
 * @author Yihleego
 */
public final class ChatUtils {

    public static ChatFactory.Box boxed(Code code) {
        return boxed(code.getCode());
    }

    public static ChatFactory.Box boxed(Code code, com.google.protobuf.Message data) {
        return boxed(code.getCode(), data);
    }

    public static ChatFactory.Box boxed(int code) {
        return ChatFactory.Box.newBuilder()
                .setCode(code)
                .build();
    }

    public static ChatFactory.Box boxed(int code, com.google.protobuf.Message data) {
        return ChatFactory.Box.newBuilder()
                .setCode(code)
                .setData(Any.newBuilder()
                        .setValue(ByteString.copyFrom(data.toByteArray()))
                        .build())
                .build();
    }

}
