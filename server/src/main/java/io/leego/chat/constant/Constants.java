package io.leego.chat.constant;

import io.netty.util.AttributeKey;

/**
 * @author Yihleego
 */
public final class Constants {
    public static final String ACCESS_TOKEN = "access_token";
    public static final AttributeKey<Object> ATTR_USER = AttributeKey.valueOf("USER");
    public static final AttributeKey<Object> ATTR_USER_ID = AttributeKey.valueOf("USER_ID");

    private Constants() {
    }
}
