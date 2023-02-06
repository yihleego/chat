package io.leego.mock.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Leego Yih
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SignUpDTO extends DeviceDTO {
    @NotEmpty
    @Size(min = 4, max = 20)
    private String username;
    @NotEmpty
    @Size(min = 4, max = 20)
    private String password;
    @NotEmpty
    @Size(min = 2, max = 20)
    private String nickname;
}
