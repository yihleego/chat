package io.leego.chat.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Leego Yih
 */
@Getter
@AllArgsConstructor
public enum MemberStatus {
    JOINED((short) 0),
    LEFT((short) 1),
    REMOVED((short) 2),
    ;
    private final short code;
}
