package io.leego.chat.config;

import io.leego.chat.server.ChatServer;
import io.leego.chat.server.SocketChatServer;
import io.leego.chat.server.WebSocketChatServer;
import io.leego.chat.server.handle.AuthHandler;
import io.leego.chat.server.handle.ChatServerHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Yihleego
 */
@Configuration
@ComponentScan(basePackages = "io.leego.chat")
@EnableConfigurationProperties(ChatProperties.class)
public class ChatConfiguration implements InitializingBean, DisposableBean {
    @Autowired
    private ChatServer chatServer;

    @Bean
    public AuthHandler authenticationHandler() {
        return new AuthHandler();
    }

    @Bean
    public ChatServerHandler chatServerHandler() {
        return new ChatServerHandler();
    }

    @Bean
    public ChatServer chatServer(ChatProperties properties) {
        if (properties.getWebsocket().isEnabled()) {
            return createWebSocketChatServer(properties);
        } else {
            return createSocketChatServer(properties);
        }
    }

    private SocketChatServer createSocketChatServer(ChatProperties properties) {
        return new SocketChatServer(
                properties.getPort(),
                properties.getIdleTimeout(),
                properties.getAuthTimeout(),
                authenticationHandler(),
                chatServerHandler());
    }

    private WebSocketChatServer createWebSocketChatServer(ChatProperties properties) {
        return new WebSocketChatServer(
                properties.getPort(),
                properties.getWebsocket().getPath(),
                properties.getIdleTimeout(),
                properties.getAuthTimeout(),
                authenticationHandler(),
                chatServerHandler());
    }

    @Override
    public void afterPropertiesSet() {
        chatServer.start();
    }

    @Override
    public void destroy() {
        chatServer.stop();
    }

}
