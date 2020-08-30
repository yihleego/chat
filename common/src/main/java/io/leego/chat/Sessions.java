package io.leego.chat;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Yihleego
 */
public final class Sessions {
    private static final ConcurrentMap<String, UserDetail> SESSIONS = new ConcurrentHashMap<>();

    static {
        SESSIONS.put("dante", new UserDetail(1L, "dante", "dante"));
        SESSIONS.put("virgil", new UserDetail(2L, "virgil", "virgil"));
        SESSIONS.put("nero", new UserDetail(3L, "nero", "nero"));
    }

    public static UserDetail get(String token) {
        if (token == null) {
            return null;
        }
        return SESSIONS.get(token);
    }

}
