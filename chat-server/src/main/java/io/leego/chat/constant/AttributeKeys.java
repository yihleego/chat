package io.leego.chat.constant;

import io.netty.util.AttributeKey;

/**
 * @author Leego Yih
 */
public final class AttributeKeys {
    public static final AttributeKey<Long> USER_ID = AttributeKey.valueOf("USER_ID");
    public static final AttributeKey<Integer> CLIENT = AttributeKey.valueOf("CLIENT");
    public static final AttributeKey<Boolean> KICKED_OUT = AttributeKey.valueOf("KICKED_OUT");
}
