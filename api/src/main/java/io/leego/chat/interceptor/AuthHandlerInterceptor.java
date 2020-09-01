package io.leego.chat.interceptor;

import io.leego.chat.UserDetail;
import io.leego.chat.util.UserUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        UserDetail user = UserUtils.getUser();
        if (user != null) {
            return true;
        }
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return false;
    }

}
