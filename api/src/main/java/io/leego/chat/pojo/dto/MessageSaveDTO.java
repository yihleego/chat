package io.leego.chat.pojo.dto;

/**
 * @author Yihleego
 */
public class MessageSaveDTO {
    private Long recipient;
    private String content;
    private Short type;

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

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }
}
