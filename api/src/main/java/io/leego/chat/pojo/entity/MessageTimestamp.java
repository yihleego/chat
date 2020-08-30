package io.leego.chat.pojo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * @author Yihleego
 */
@Document("message_timestamp")
public class MessageTimestamp {
    @Id
    private String id;
    @Indexed(unique = true)
    private Long owner;
    private LocalDateTime time;

    public MessageTimestamp() {
    }

    public MessageTimestamp(String id, Long owner, LocalDateTime time) {
        this.id = id;
        this.owner = owner;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
