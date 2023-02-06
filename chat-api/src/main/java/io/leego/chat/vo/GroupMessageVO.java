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
public class GroupMessageVO {
    private Long id;
    private Long groupId;
    private Long sender;
    private Short type;
    private String content;
    private MentionVO[] mentions;
    private boolean taken;
    private boolean seen;
    private boolean revoked;
    private Instant sentTime;
    private Short status;
}
