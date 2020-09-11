package io.leego.chat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author Yihleego
 */
@ConfigurationProperties("chat.server")
public class ChatProperties {
    private Integer port = 20000;
    private String path = "";
    private Duration idleTimeout = Duration.ofSeconds(60L);
    private Duration authTimeout = Duration.ofSeconds(5L);

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Duration getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(Duration idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public Duration getAuthTimeout() {
        return authTimeout;
    }

    public void setAuthTimeout(Duration authTimeout) {
        this.authTimeout = authTimeout;
    }

}
