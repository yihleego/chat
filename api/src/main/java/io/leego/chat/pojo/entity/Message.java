package io.leego.chat.pojo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * @author Yihleego
 */
@Document("message")
@CompoundIndex(name = "idx_sender_time", def = "{'sender':1,'time':1}")
@CompoundIndex(name = "idx_recipient_time", def = "{'recipient':1,'time':1}")
public class Message {
    @Id
    private String id;
    private Long sender;
    private Long recipient;
    private String content;
    private LocalDateTime time;
    private Short type;
    private Short status;

    public Message() {
    }

    public Message(String id, Long sender, Long recipient, String content, LocalDateTime time, Short type, Short status) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.time = time;
        this.type = type;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getSender() {
        return sender;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public Long getRecipient() {
        return recipient;
    }

    public void setRecipient(Long recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }
}
