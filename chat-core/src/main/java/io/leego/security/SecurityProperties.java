package io.leego.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Leego Yih
 */
@Data
@ConfigurationProperties("spring.security.redis")
public class SecurityProperties {
    /**
     * Redis server host.
     */
    private String host = "localhost";
    /**
     * Redis server port.
     */
    private int port = 6379;
    /**
     * Whether to enable SSL support.
     */
    private boolean ssl = false;
    /**
     * Login username of the redis server.
     */
    private String username;
    /**
     * Login password of the redis server.
     */
    private String password;
    /**
     * Database index used by the connection factory.
     */
    private int database = 0;
}
