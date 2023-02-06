package io.leego.chat.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Leego Yih
 */
@Getter
@AllArgsConstructor
public enum MessageType {
    TEXT((short) 0),
    IMAGE((short) 1),
    VIDEO((short) 2),
    AUDIO((short) 3),
    VOICE((short) 4),
    FILE((short) 5),
    STICKER((short) 6),
    LOCATION((short) 7),
    SHARE((short) 8),
    ;
    private final short code;

    private static final Map<Short, MessageType> map = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(MessageType::getCode, Function.identity()));

    public static MessageType get(Short code) {
        return map.get(code);
    }
}
