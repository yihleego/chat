package io.leego.chat.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yihleego
 */
public enum Code {
    UNKNOWN(0, "Unknown"),
    ERROR(1, "Error"),
    HEARTBEAT(2, "Heartbeat"),
    AUTHENTICATION(3, "Authentication"),
    AUTHENTICATED(4, "Authenticated"),
    UNAUTHENTICATED(5, "Unauthenticated"),
    SEND_MESSAGE(10, "Send Message"),
    RECEIVE_MESSAGE(11, "Receive Message"),
    RECEIVED_MESSAGE(12, "Received Message"),
    DELIVERED_MESSAGE(13, "Delivered Message"),
    DELIVERED_OFFLINE_MESSAGE(14, "Delivered Offline Message");

    private final int code;
    private final String value;

    Code(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    private static final Map<Integer, Code> map = new HashMap<>(64);

    static {
        for (Code e : values()) {
            map.put(e.code, e);
        }
    }

    public static Code get(Integer code) {
        return map.get(code);
    }

    public static Code getOrDefault(Integer code, Code defaultValue) {
        return map.getOrDefault(code, defaultValue);
    }

}
