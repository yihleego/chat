package io.leego.chat.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Leego Yih
 */
@Getter
@AllArgsConstructor
public enum GroupMessageStatus {
    SENDING((short) 0),
    READY((short) 1),
    ;
    private final short code;
}
