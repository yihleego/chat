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
import org.hibernate.annotations.Where;

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
@Table(name = "chat_group_member")
@Where(clause = "deleted=0")
public class GroupMember extends CrudEntity<Long> {
    @Column(nullable = false, updatable = false)
    private Long groupId;
    @Column(nullable = false, updatable = false)
    private Long userId;
    private String alias;
    @Column(nullable = false)
    private Short status;
}
