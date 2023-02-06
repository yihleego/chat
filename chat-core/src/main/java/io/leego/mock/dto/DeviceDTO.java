package io.leego.mock.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Leego Yih
 */
@Data
public class DeviceDTO {
    @NotNull
    private Long deviceId;
    @NotNull
    private Short deviceType;
    @NotNull
    private Short clientType;
}
