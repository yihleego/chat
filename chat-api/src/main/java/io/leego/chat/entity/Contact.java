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
@Table(name = "chat_contact")
@Where(clause = "deleted=0")
public class Contact extends CrudEntity<Long> {
    @Column(nullable = false, updatable = false)
    private Long sender;
    @Column(nullable = false, updatable = false)
    private Long recipient;
    private String alias;
    @Column(nullable = false)
    private Short status;
}
