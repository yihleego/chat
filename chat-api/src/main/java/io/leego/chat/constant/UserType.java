package io.leego.chat.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Leego Yih
 */
@Getter
@AllArgsConstructor
public enum UserType {
    SENDER((short) 0),
    RECIPIENT((short) 1),
    ;
    private final short code;

    public static UserType get(Short code) {
        if (code == null) {
            return null;
        }
        return code == 0 ? SENDER : RECIPIENT;
    }
}
