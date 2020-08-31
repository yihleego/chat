package io.leego.chat.constant;

import io.leego.chat.UserDetail;
import io.netty.util.AttributeKey;

/**
 * @author Yihleego
 */
public final class Constants {
    public static final String ACCESS_TOKEN = "access_token";
    public static final AttributeKey<UserDetail> ATTR_USER = AttributeKey.valueOf("ATTR_USER");
    public static final AttributeKey<Long> ATTR_USER_ID = AttributeKey.valueOf("ATTR_USER_ID");
    public static final AttributeKey<Boolean> ATTR_REMOVED = AttributeKey.valueOf("ATTR_REMOVED");

    private Constants() {
    }
}
