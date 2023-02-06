package io.leego.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * @author Leego Yih
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfiguration {

    @Bean
    public SecurityManager securityManager(SecurityProperties properties) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(properties.getHost());
        configuration.setPort(properties.getPort());
        configuration.setUsername(properties.getUsername());
        configuration.setPassword(properties.getPassword());
        configuration.setDatabase(properties.getDatabase());
        LettuceClientConfiguration clientConfiguration = properties.isSsl()
                ? LettuceClientConfiguration.builder().useSsl().build()
                : LettuceClientConfiguration.builder().build();
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(configuration, clientConfiguration);
        connectionFactory.afterPropertiesSet();
        return new SecurityManager(connectionFactory);
    }

}
