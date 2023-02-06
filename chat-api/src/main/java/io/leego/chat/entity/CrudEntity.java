package io.leego.chat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

/**
 * @author Leego Yih
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
@MappedSuperclass
public abstract class CrudEntity<ID> extends BaseEntity<ID> implements Creatable, Updatable, Deletable<ID> {
    @CreatedDate
    @Column(nullable = false, updatable = false)
    protected Instant createdTime;
    @LastModifiedDate
    protected Instant updatedTime;
    @Column(insertable = false, updatable = false)
    protected ID deleted;
    @Column(insertable = false, updatable = false)
    protected Instant deletedTime;

    /**
     * Returns {@code true} if it has been deleted,
     * that is, when the {@code deleted} is equal to the {@code id}.
     */
    @Override
    public boolean isDeleted() {
        return deleted != null && deleted.equals(id);
    }
}
