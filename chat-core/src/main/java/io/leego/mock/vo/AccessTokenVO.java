package io.leego.mock.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenVO {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
}
