package io.leego.chat.util;

import io.leego.chat.security.UserDetail;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public final class UserUtils {
    private static final String USER_SESSION = "user_session";

    public static UserDetail getUser() {
        return getAttribute(USER_SESSION, UserDetail.class);
    }

    public static Long getUserId() {
        UserDetail userDetail = getUser();
        if (userDetail == null) {
            return null;
        }
        return userDetail.getId();
    }

    public static void setUser(UserDetail user) {
        setAttribute(USER_SESSION, user);
    }

    public static void removeUser() {
        removeAttribute(USER_SESSION);
    }


    private static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    private static Object getAttribute(String name) {
        return getHttpServletRequest().getSession().getAttribute(name);
    }

    private static <T> T getAttribute(String name, Class<T> type) {
        Object o = getAttribute(name);
        if (o == null) {
            return null;
        }
        return type.cast(o);
    }

    private static void setAttribute(String name, Object o) {
        getHttpServletRequest().getSession().setAttribute(name, o);
    }

    private static void removeAttribute(String name) {
        getHttpServletRequest().getSession().removeAttribute(name);
    }

}
