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
public enum DeviceType {
    MAC((short) 1, "Mac"),
    WINDOWS((short) 2, "Windows"),
    LINUX((short) 3, "Linux"),
    ANDROID((short) 4, "Android"),
    IOS((short) 5, "iOS"),
    BROWSER((short) 6, "Browser"),
    ;
    private final short code;
    private final String name;

    private static final Map<Short, DeviceType> map = Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(DeviceType::getCode, Function.identity()));

    public static DeviceType get(Short code) {
        return map.get(code);
    }
}
