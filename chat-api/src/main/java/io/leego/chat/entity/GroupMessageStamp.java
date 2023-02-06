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
@Table(name = "chat_group_message_stamp")
public class GroupMessageStamp extends BaseEntity<Long> {
    @Column(nullable = false, updatable = false)
    private Long userId;
    @Column(nullable = false, updatable = false)
    private Short userType;
    @Column(nullable = false, updatable = false)
    private Long deviceId;
    @Column(nullable = false, updatable = false)
    private Short deviceType;
    @Column(nullable = false, updatable = false)
    private Short clientType;
    @Column(nullable = false, updatable = true)
    private Long messageId;
    @Column(nullable = false, updatable = true)
    private Instant lastTime;
}
