package io.leego.chat.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Transient;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author Leego Yih
 */
@ToString
@EqualsAndHashCode
@FieldNameConstants
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity<ID> implements Entity<ID>, Persistable<ID> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected ID id;
    @Transient
    private transient boolean _new = true;

    /**
     * Returns the id of the entity.
     * Overrides against {@link Nullable} annotated method {@link Persistable#getId()}.
     *
     * @return the id of the entity.
     */
    @NonNull
    @Override
    public ID getId() {
        return id;
    }

    @Override
    public void setId(ID id) {
        this.id = id;
    }

    /**
     * Persists the entity if the returned value is {@code true},
     * otherwise merge the entity.
     *
     * @see jakarta.persistence.EntityManager#persist(Object)
     * @see jakarta.persistence.EntityManager#merge(Object)
     */
    @Override
    public boolean isNew() {
        return _new;
    }

    @PostLoad
    void postLoad() {
        this._new = false;
    }

    public void makeNew() {
        this._new = true;
    }

    public void makeNotNew() {
        this._new = false;
    }

    /**
     * Merge the non-null property values of the given entity into self,
     * ignoring the given properties.
     */
    public <T> void merge(T entity, String... ignoreProperties) {
        // It is guaranteed that the class of the entity equals or inherits this class
        if (!this.getClass().isInstance(entity)) {
            throw new IllegalArgumentException();
        }
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(this.getClass());
        for (PropertyDescriptor pd : pds) {
            if (IGNORED.contains(pd.getName()) || ObjectUtils.containsElement(ignoreProperties, pd.getName())) {
                continue;
            }
            Method writeMethod = pd.getWriteMethod();
            if (writeMethod != null) {
                Method readMethod = pd.getReadMethod();
                if (readMethod != null) {
                    try {
                        // Merge non-null property values only
                        Object value = readMethod.invoke(entity);
                        if (value != null) {
                            writeMethod.invoke(this, value);
                        }
                    } catch (Throwable e) {
                        throw new FatalBeanException("Could not merge property '" + pd.getName() + "'", e);
                    }
                }
            }
        }
    }

    public static final Set<String> IGNORED = Set.of("class", "new", "_new", "id", "createdTime", "updatedTime", "deleted", "deletedTime");
}
