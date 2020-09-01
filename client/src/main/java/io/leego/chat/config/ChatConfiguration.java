package io.leego.chat.config;

import io.leego.chat.client.ChatClient;
import io.leego.chat.client.handler.ChatClientHandler;
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
    private ChatClient chatClient;

    @Bean
    public ChatClientHandler chatClientHandler() {
        return new ChatClientHandler();
    }

    @Bean
    public ChatClient chatClient(ChatProperties properties) {
        return new ChatClient(properties.getHost(), properties.getPort(), properties.getIdleTimeout(), chatClientHandler());
    }

    @Override
    public void afterPropertiesSet() {
        chatClient.start();
    }

    @Override
    public void destroy() {
        chatClient.stop();
    }

}
