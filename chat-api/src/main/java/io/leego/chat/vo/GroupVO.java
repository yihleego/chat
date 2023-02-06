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
public class GroupVO {
    private Long id;
    private String name;
    private String avatar;
    private Long owner;
    private Instant createdTime;
    private Instant updatedTime;
}
