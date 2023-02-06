package io.leego.chat.config;

import io.leego.chat.constant.MessageType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.Duration;
import java.util.Map;

/**
 * @author Leego Yih
 */
@Data
@ConfigurationProperties("chat")
public class ChatProperties {
    @NestedConfigurationProperty
    private Message message = new Message();
    @NestedConfigurationProperty
    private Contact contact = new Contact();
    @NestedConfigurationProperty
    private Group group = new Group();
    @NestedConfigurationProperty
    private Member member = new Member();
    @NestedConfigurationProperty
    private Registry registry = new Registry();

    /** Message */
    @Data
    public static class Message {
        private Duration obtainTimeout = Duration.ofDays(1);
        private Duration revokeTimeout = Duration.ofMinutes(3);
        private Duration historyTimeout = Duration.ofDays(100);
        private int fetchSize = 200;
        private int batchSize = 200;
        private Map<MessageType, Integer> contentSize = Map.of(
                MessageType.TEXT, 2000,
                MessageType.IMAGE, 20,
                MessageType.STICKER, 100,
                MessageType.SHARE, 2000
        );
    }

    /** Contact */
    @Data
    public static class Contact {
        private String key = "contacts:";
        private int fetchSize = 200;
        private int maxSize = 5000;
    }

    /** Group */
    @Data
    public static class Group {
        private int maxSize = 100;
    }

    /** Member */
    @Data
    public static class Member {
        private String key = "members:";
        private int fetchSize = 200;
        private int maxSize = 1000;
    }

    /** Registry */
    @Data
    public static class Registry {
        private String key = "instances";
        private Duration timeToLive = Duration.ofMinutes(10);
    }
}
