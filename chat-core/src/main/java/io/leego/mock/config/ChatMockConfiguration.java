package io.leego.mock.config;

import io.leego.mock.interceptor.AuthHandlerInterceptor;
import io.leego.security.SecurityConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Leego Yih
 */
@AutoConfiguration
@Import(SecurityConfiguration.class)
public class ChatMockConfiguration {

    @Configuration
    @ConditionalOnClass(JpaRepositoriesAutoConfiguration.class)
    @EnableJpaRepositories({"io.leego.chat.repository", "io.leego.mock.repository"})
    @EntityScan({"io.leego.chat.entity", "io.leego.mock.entity"})
    public static class JpaMockConfiguration {
    }

    @Configuration
    @ConditionalOnClass({WebMvcConfigurer.class, HandlerInterceptor.class})
    @ComponentScan({"io.leego.mock.*"})
    public static class WebMvcConfiguration implements WebMvcConfigurer {
        private final AuthHandlerInterceptor authHandlerInterceptor;

        public WebMvcConfiguration(AuthHandlerInterceptor authHandlerInterceptor) {
            this.authHandlerInterceptor = authHandlerInterceptor;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(authHandlerInterceptor);
        }
    }

}
