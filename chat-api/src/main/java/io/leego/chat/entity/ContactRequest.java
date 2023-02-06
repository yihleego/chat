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
@Table(name = "chat_contact_request")
public class ContactRequest extends UpdatableEntity<Long> {
    @Column(nullable = false, updatable = false)
    private Long sender;
    @Column(nullable = false, updatable = false)
    private Long recipient;
    private String message;
    @Column(nullable = false)
    private Short status;
}
