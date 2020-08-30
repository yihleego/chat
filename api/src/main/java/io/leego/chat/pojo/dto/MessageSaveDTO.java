package io.leego.chat.pojo.dto;

import javax.validation.constraints.NotNull;

/**
 * @author Yihleego
 */
public class MessageSaveDTO {
    @NotNull
    private Long recipient;
    @NotNull
    private String content;

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
}
