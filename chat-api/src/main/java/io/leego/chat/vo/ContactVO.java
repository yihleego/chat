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
public class ContactVO {
    private Long id;
    private Long recipient;
    private String nickname;
    private String avatar;
    private String alias;
    private Instant createdTime;
    private Instant updatedTime;
}
