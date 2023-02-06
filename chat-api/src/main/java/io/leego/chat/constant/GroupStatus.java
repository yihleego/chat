package io.leego.chat.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Leego Yih
 */
@Getter
@AllArgsConstructor
public enum GroupStatus {
    ACTIVE((short) 0),
    DELETED((short) 1),
    BANNED((short) 2),
    ;
    private final short code;
}
