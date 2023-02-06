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
public class GroupMemberVO {
    private Long userId;
    private String nickname;
    private String alias;
    private Instant createdTime;
    private Instant updatedTime;
}
