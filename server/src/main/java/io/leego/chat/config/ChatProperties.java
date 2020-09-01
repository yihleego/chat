package io.leego.chat.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.Duration;

/**
 * @author Yihleego
 */
@ConfigurationProperties("chat.server")
public class ChatProperties {
    private Integer port = 20000;
    private Duration idleTimeout = Duration.ofSeconds(60L);
    private Duration authTimeout = Duration.ofSeconds(5L);
    @NestedConfigurationProperty
    private Socket socket = new Socket();
    @NestedConfigurationProperty
    private Websocket websocket = new Websocket();

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

    public Duration getAuthTimeout() {
        return authTimeout;
    }

    public void setAuthTimeout(Duration authTimeout) {
        this.authTimeout = authTimeout;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Websocket getWebsocket() {
        return websocket;
    }

    public void setWebsocket(Websocket websocket) {
        this.websocket = websocket;
    }

    protected static class Socket {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    protected static class Websocket {
        private boolean enabled = false;
        private String path = "";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

}
