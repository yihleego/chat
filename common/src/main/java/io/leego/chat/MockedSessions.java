package io.leego.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Yihleego
 */
public final class MockedSessions {
    private static final ConcurrentMap<String, UserDetail> SESSIONS = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, UserDetail> DATA = new ConcurrentHashMap<>();
    private static final List<UserDetail> USERS;

    static {
        List<UserDetail> users = new ArrayList<>();
        users.add(new UserDetail(1L, "dante", "dante", "b8d7df26-3c72-4cd2-9db9-d5fc28e42912"));
        users.add(new UserDetail(2L, "vergil", "vergil", "be343f86-db26-4093-95a8-675560e6bb17"));
        users.add(new UserDetail(3L, "nero", "nero", "3fff937c-8354-4d21-88fb-edc429cb3f34"));
        users.add(new UserDetail(4L, "sparda", "sparda", "5f40f485-e81b-4fc3-b37c-4c098c241baa"));
        users.add(new UserDetail(5L, "lady", "lady", "bc8124a0-44c4-4c70-b4c3-adabeba349fd"));
        users.add(new UserDetail(6L, "trish", "trish", "33120b3a-c33f-4c5b-adbb-e0fe5a187615"));
        users.add(new UserDetail(7L, "lucia", "lucia", "8a3d7286-8a3b-471b-b95d-b1304abbc60f"));
        users.add(new UserDetail(8L, "beryl", "beryl", "8634e4e3-0f1b-47c0-837a-6bad68e274ac"));
        users.add(new UserDetail(9L, "credo", "credo", "5bc97e33-02e7-438a-abec-48576bdf8b1f"));
        users.add(new UserDetail(10L, "kyrie", "kyrie", "64c5a796-cb39-4531-8a8b-94c5df693424"));
        users.add(new UserDetail(11L, "nico", "nico", "10f935eb-831a-46b0-a109-d6672b5111f3"));
        for (UserDetail u : users) {
            SESSIONS.put(u.getToken(), u);
            DATA.put(u.getUsername(), u);
        }
        USERS = Collections.unmodifiableList(users);
    }

    public static UserDetail getByToken(String token) {
        if (token == null) {
            return null;
        }
        return SESSIONS.get(token);
    }

    public static UserDetail getByUsername(String token) {
        if (token == null) {
            return null;
        }
        return DATA.get(token);
    }

    public static List<UserDetail> getUsers() {
        return USERS;
    }

}
