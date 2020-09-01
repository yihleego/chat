package io.leego.chat.util;

import io.leego.chat.UserDetail;
import io.netty.util.AttributeKey;

/**
 * @author Yihleego
 */
public final class AttrKey {
    public static final AttributeKey<UserDetail> ATTR_USER = AttributeKey.valueOf("ATTR_USER");
    public static final AttributeKey<Long> ATTR_USER_ID = AttributeKey.valueOf("ATTR_USER_ID");
    public static final AttributeKey<Boolean> ATTR_REMOVED = AttributeKey.valueOf("ATTR_REMOVED");

    private AttrKey() {
    }
}
