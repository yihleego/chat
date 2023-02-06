package io.leego.chat.dto;

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
public class AudioDTO {
    @NotEmpty
    private String url;
    @NotEmpty
    private String filename;
    @NotNull
    private Integer size;
    @NotNull
    private Integer duration;
}

