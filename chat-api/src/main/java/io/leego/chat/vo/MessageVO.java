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
public class MessageVO {
    private Long id;
    private Long sender;
    private Long recipient;
    private Short type;
    private String content;
    private boolean taken;
    private boolean seen;
    private boolean revoked;
    private Instant sentTime;
}
