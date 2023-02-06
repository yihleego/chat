package io.leego.chat.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Leego Yih
 */
@Getter
@AllArgsConstructor
public enum ContactStatus {
    ADDED((short) 0),
    BLOCKED((short) 1),
    REMOVED((short) 2),
    ;
    private final short code;
}
