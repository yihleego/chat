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
public enum ClientType {
    DESKTOP((short) 1, "Desktop", true),
    MOBILE((short) 2, "Mobile App", true),
    WEB((short) 3, "Web", false),
    ;
    private final short code;
    private final String name;
    private final boolean capable;

    private static final Map<Short, ClientType> map = Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(ClientType::getCode, Function.identity()));

    public static ClientType get(Short code) {
        return map.get(code);
    }
}
