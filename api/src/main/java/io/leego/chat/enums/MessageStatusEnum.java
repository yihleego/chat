package io.leego.chat.enums;

/**
 * @author Yihleego
 */
public enum MessageStatusEnum {
    READ((short) 1),
    UNREAD((short) 2),
    REVOKED((short) 3);

    private final short code;

    MessageStatusEnum(short code) {
        this.code = code;
    }

    public short getCode() {
        return code;
    }

}
