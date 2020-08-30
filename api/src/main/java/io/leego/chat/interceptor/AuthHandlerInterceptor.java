package io.leego.chat.interceptor;

import io.leego.chat.Sessions;
import io.leego.chat.UserDetail;
import io.leego.chat.util.UserUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthHandlerInterceptor implements HandlerInterceptor {
    private static final String ACCESS_TOKEN = "access_token";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = getCookie(request, ACCESS_TOKEN);
        if (token != null) {
            UserDetail user = Sessions.get(token);
            if (user != null) {
                UserUtils.setUser(user);
                return true;
            }
        }
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        UserUtils.removeUser();
    }

    public static String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length <= 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie != null) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
