package io.leego.chat.constant;

import io.leego.chat.core.Box;

/**
 * Fixed messages
 *
 * @author Leego Yih
 */
public final class Messages {
    public static final Box HEARTBEAT = Box.newBuilder().setCode(Codes.HEARTBEAT).build();
    public static final Box KICKED_OUT = Box.newBuilder().setCode(Codes.KICKED_OUT).build();
    public static final Box AUTHENTICATED = Box.newBuilder().setCode(Codes.AUTHENTICATED).build();
    public static final Box UNAUTHENTICATED = Box.newBuilder().setCode(Codes.UNAUTHENTICATED).build();
}
