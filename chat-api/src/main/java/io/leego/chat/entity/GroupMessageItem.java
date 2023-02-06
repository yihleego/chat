package io.leego.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.time.Instant;

/**
 * @author Leego Yih
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@FieldNameConstants
@Entity
@Table(name = "chat_group_message_item")
public class GroupMessageItem extends BaseEntity<Long> {
    @Column(nullable = false, updatable = false)
    private Long messageId;
    @Column(nullable = false, updatable = false)
    private Long recipient;
    @Column(nullable = false, updatable = true)
    private Instant eventTime;
    @Column(nullable = true, insertable = false)
    private Instant takenTime;
    @Column(nullable = true, insertable = false)
    private Instant seenTime;
}
