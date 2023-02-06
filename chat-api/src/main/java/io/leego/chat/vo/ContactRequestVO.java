package io.leego.chat.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * @author Leego Yih
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequestVO {
    private Long id;
    private String nickname;
    private String avatar;
    private String message;
    private Instant createdTime;
    private Instant updatedTime;
}
