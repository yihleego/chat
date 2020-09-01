package io.leego.chat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author Yihleego
 */
@ConfigurationProperties("chat.client")
public class ChatProperties {
    private String host = "localhost";
    private Integer port = 20000;
    private Duration idleTimeout = Duration.ofSeconds(30L);

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Duration getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(Duration idleTimeout) {
        this.idleTimeout = idleTimeout;
    }
}
