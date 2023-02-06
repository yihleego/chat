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
public class GroupMessagePrimeVO {
    private Long id;
    private Instant sentTime;
    private Short status;
}
