package io.leego.chat.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Leego Yih
 */
@Getter
@AllArgsConstructor
public enum RequestStatus {
    APPLIED((short) 0),
    ACCEPTED((short) 1),
    REJECTED((short) 2),
    ;
    private final short code;
}
