package io.leego.chat.constant;

/**
 * Frequently used codes should preferably be less than {@code 128}.
 *
 * @author Leego Yih
 */
public final class Codes {
    /** Basic */
    public static final int UNKNOWN = 0;
    public static final int HEARTBEAT = 1;
    public static final int KICKED_OUT = 2;
    public static final int AUTHENTICATION = 3;
    public static final int AUTHENTICATED = 4;
    public static final int UNAUTHENTICATED = 5;
    /** Message */
    public static final int MESSAGE_SEND_NOTIFY = 10;        // sender    -> server
    public static final int MESSAGE_SEND_PUSH = 11;          // server    -> recipient
    public static final int MESSAGE_TAKE_NOTIFY = 12;        // recipient -> server
    public static final int MESSAGE_TAKE_PUSH = 13;          // server    -> sender
    public static final int MESSAGE_TAKE_OFFLINE_PUSH = 14;  // server    -> sender
    public static final int MESSAGE_READ_NOTIFY = 15;        // recipient -> server
    public static final int MESSAGE_READ_PUSH = 16;          // server    -> sender
    public static final int MESSAGE_READ_BATCH_NOTIFY = 17;  // recipient -> server
    public static final int MESSAGE_READ_BATCH_PUSH = 18;    // server    -> sender
    public static final int MESSAGE_REVOKE_NOTIFY = 19;      // sender    -> server
    public static final int MESSAGE_REVOKE_PUSH = 20;        // server    -> recipient
    public static final int MESSAGE_REMOVE_NOTIFY = 21;      // recipient -> server
    public static final int MESSAGE_REMOVE_PUSH = 22;        // server    -> sender
    public static final int MESSAGE_REMOVE_OFFLINE_PUSH = 23;// server    -> sender
    public static final int MESSAGE_SYNC_PUSH = 24;          // server    -> sender
    /** Group Message */
    public static final int GROUP_MESSAGE_SEND_NOTIFY = 30;        // sender    -> server
    public static final int GROUP_MESSAGE_SEND_PUSH = 31;          // server    -> recipient
    public static final int GROUP_MESSAGE_TAKE_NOTIFY = 32;        // recipient -> server
    public static final int GROUP_MESSAGE_TAKE_PUSH = 33;          // server    -> sender
    public static final int GROUP_MESSAGE_TAKE_OFFLINE_PUSH = 34;  // server    -> sender
    public static final int GROUP_MESSAGE_READ_NOTIFY = 35;        // recipient -> server
    public static final int GROUP_MESSAGE_READ_PUSH = 36;          // server    -> sender
    public static final int GROUP_MESSAGE_READ_BATCH_NOTIFY = 37;  // recipient -> server
    public static final int GROUP_MESSAGE_READ_BATCH_PUSH = 38;    // server    -> sender
    public static final int GROUP_MESSAGE_REVOKE_NOTIFY = 39;      // sender    -> server
    public static final int GROUP_MESSAGE_REVOKE_PUSH = 40;        // server    -> recipient
    public static final int GROUP_MESSAGE_REMOVE_NOTIFY = 41;      // recipient -> server
    public static final int GROUP_MESSAGE_REMOVE_PUSH = 42;        // server    -> sender
    public static final int GROUP_MESSAGE_REMOVE_OFFLINE_PUSH = 43;// server    -> sender
    public static final int GROUP_MESSAGE_SYNC_PUSH = 44;          // server    -> sender
    /** Contact */
    public static final int CONTACT_REQUEST_SEND_NOTIFY = 50;      // sender    -> server
    public static final int CONTACT_REQUEST_SEND_PUSH = 51;        // server    -> recipient
    public static final int CONTACT_REQUEST_TAKE_NOTIFY = 52;      // recipient -> server
    public static final int CONTACT_REQUEST_TAKE_PUSH = 53;        // server    -> sender
    public static final int CONTACT_REQUEST_TAKE_OFFLINE_PUSH = 54;// server    -> sender
    public static final int CONTACT_EVENT_NOTIFY = 55;             // sender    -> server
    public static final int CONTACT_EVENT_PUSH = 56;               // server    -> recipient
    /** Group */
    public static final int GROUP_EVENT_NOTIFY = 60;
    public static final int GROUP_EVENT_PUSH = 61;
    public static final int GROUP_MEMBER_EVENT_NOTIFY = 62;
    public static final int GROUP_MEMBER_EVENT_PUSH = 63;
}
