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
@Table(name = "chat_group")
public class Group extends UpdatableEntity<Long> {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String avatar;
    @Column(nullable = false)
    private Long owner;
    @Column(nullable = false)
    private Integer size;
    @Column(nullable = false)
    private Short status;
}
