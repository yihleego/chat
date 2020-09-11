package io.leego.chat.pojo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * @author Yihleego
 */
@Document("message_timestamp")
public class MessageTimestamp {
    @Id
    private String id;
    @Indexed(unique = true)
    @Field("user_id")
    private Long userId;
    private LocalDateTime time;

    public MessageTimestamp() {
    }

    public MessageTimestamp(String id, Long userId, LocalDateTime time) {
        this.id = id;
        this.userId = userId;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
