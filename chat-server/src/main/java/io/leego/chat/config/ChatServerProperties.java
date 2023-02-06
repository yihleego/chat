package io.leego.chat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.Duration;

/**
 * @author Leego Yih
 */
@Data
@ConfigurationProperties("chat.server")
public class ChatServerProperties {
    @NestedConfigurationProperty
    private Raw raw = new Raw();
    @NestedConfigurationProperty
    private Ws ws = new Ws();
    private Duration authTimeout = Duration.ofSeconds(5);
    private Duration idleTimeout = Duration.ofMinutes(5);
    /** Maximum number of connections for each instance */
    private int connectionSize = (1 << 17) - 1; // 131071
    private int nettyThreadSize = 0;
    private int workThreadSize = 0;
    private Mode mode = Mode.SHARED;
    @NestedConfigurationProperty
    private Cluster cluster = new Cluster();
    @NestedConfigurationProperty
    private Discovery discovery = new Discovery();
    @NestedConfigurationProperty
    private Registry registry = new Registry();
    @NestedConfigurationProperty
    private Meta meta = new Meta();
    @NestedConfigurationProperty
    private Contact contact = new Contact();
    @NestedConfigurationProperty
    private Member member = new Member();

    /** Raw TCP */
    @Data
    public static class Raw {
        private boolean enabled = true;
        private int port = 10000;
    }

    /** WebSocket */
    @Data
    public static class Ws {
        private boolean enabled = true;
        private int port = 10001;
        private String path = "/";
    }

    /** Cluster */
    @Data
    public static class Cluster {
        private boolean enabled = true;
    }

    /** Discovery */
    @Data
    public static class Discovery {
        private int port = 20000;
        private String host;
    }

    /** Registry */
    @Data
    public static class Registry {
        private String key = "instances";
        private String seq = "node-seq";
        private Duration timeToLive = Duration.ofMinutes(10);
        private Duration pullPeriod = Duration.ofMinutes(1);
        private Duration pushPeriod = Duration.ofMinutes(3);
    }

    /** Meta */
    @Data
    public static class Meta {
        private String key = "meta:";
    }

    /** Contact */
    @Data
    public static class Contact {
        private String key = "contacts:";
    }

    /** Member */
    @Data
    public static class Member {
        private String key = "members:";
    }

    public enum Mode {
        /**
         * Allow multiple clients online for each user,
         * the most recently connected client will kick out the old same one.
         *
         * @see io.leego.chat.server.context.AbstractSharedContextManager
         * @see io.leego.chat.server.context.StandaloneSharedContextManager
         * @see io.leego.chat.cluster.context.ClusterSharedContextManager
         */
        SHARED,
        /**
         * Allow only one client online for each user,
         * the most recently connected client will kick out the old one.
         *
         * @see io.leego.chat.server.context.AbstractExclusiveContextManager
         * @see io.leego.chat.server.context.StandaloneExclusiveContextManager
         * @see io.leego.chat.cluster.context.ClusterExclusiveContextManager
         */
        EXCLUSIVE
    }
}
