package io.leego.chat.dto;

import io.leego.chat.validation.Message;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Message
public abstract class AbstractMessageDTO {
    @NotNull
    protected Short type;
    @NotEmpty
    protected String content;
}
