package io.leego.chat.constant;

/**
 * @author Yihleego
 */
public final class Constants {
    private Constants() {
    }

    public static final class Message {
        public static final String ID = "_id";
        public static final String SENDER = "sender";
        public static final String RECIPIENT = "recipient";
        public static final String CONTENT = "content";
        public static final String TIME = "time";
        public static final String STATUS = "status";
    }

    public static final class MessageTimestamp {
        public static final String ID = "_id";
        public static final String USER_ID = "user_id";
        public static final String TIME = "time";
    }

}
