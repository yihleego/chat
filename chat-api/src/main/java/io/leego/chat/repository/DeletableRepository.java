package io.leego.chat.repository;

import io.leego.chat.entity.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA soft-deleting specific extension of {@link JpaRepository}.
 *
 * @author Leego Yih
 */
@NoRepositoryBean
public interface DeletableRepository<T extends Entity<ID>, ID> extends JpaRepository<T, ID> {

    /**
     * Marks the entity as deleted with the given id and time of deletion.
     *
     * @param id          must not be {@literal null}.
     * @param deletedTime must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null},
     *                                  or the time of deletion is {@literal null}.
     */
    @Transactional
    @Modifying
    @Query("update #{#entityName} set deleted = id, deletedTime = ?2 where id = ?1 and deleted = 0")
    void deleteById(ID id, Instant deletedTime);

    /**
     * Marks all entities as deleted with the given ids and time of deletion.
     *
     * @param ids         must not be {@literal null}. Must not contain {@literal null} elements.
     * @param deletedTime must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal ids} or one of its elements is {@literal null},
     *                                  or the time of deletion is {@literal null}.
     */
    @Transactional
    @Modifying
    @Query("update #{#entityName} set deleted = id, deletedTime = ?2 where id in ?1 and deleted = 0")
    void deleteAllById(Iterable<? extends ID> ids, Instant deletedTime);

    /**
     * Marks all entities managed by the repository as deleted with the given time of deletion.
     *
     * @param deletedTime must not be {@literal null}.
     * @throws IllegalArgumentException in case the given time of deletion is {@literal null}.
     */
    @Transactional
    @Modifying
    @Query("update #{#entityName} set deleted = id, deletedTime = ?1 where deleted = 0")
    void deleteAll(Instant deletedTime);

    /**
     * Marks the entity as deleted with the given id.
     *
     * @param id must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}.
     */
    @Override
    @Transactional
    default void deleteById(ID id) {
        Assert.notNull(id, "The given id must not be null");
        this.deleteById(id, Instant.now());
    }

    /**
     * Marks all entities as deleted with the given ids.
     *
     * @param ids must not be {@literal null}. Must not contain {@literal null} elements.
     * @throws IllegalArgumentException in case the given {@literal ids} or one of its elements is {@literal null}.
     */
    @Override
    @Transactional
    default void deleteAllById(Iterable<? extends ID> ids) {
        Assert.notNull(ids, "The given ids must not be null");
        this.deleteAllById(ids, Instant.now());
    }

    /**
     * Marks all entities managed by the repository as deleted.
     */
    @Override
    @Transactional
    default void deleteAll() {
        this.deleteAll(Instant.now());
    }

    /**
     * Marks the given entity as deleted.
     *
     * @param entity must not be {@literal null}.
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    @Override
    @Transactional
    default void delete(T entity) {
        Assert.notNull(entity, "The given entity must not be null");
        Assert.notNull(entity.getId(), "The id of the given entity must not be null");
        this.deleteById(entity.getId(), Instant.now());
    }

    /**
     * Marks the given entities as deleted.
     *
     * @param entities must not be {@literal null}. Must not contain {@literal null} elements.
     * @throws IllegalArgumentException in case the given {@literal entities} or one of its entities is {@literal null}.
     */
    @Override
    @Transactional
    default void deleteAll(Iterable<? extends T> entities) {
        Assert.notNull(entities, "The given entities must not be null");
        List<ID> ids = new ArrayList<>();
        for (T entity : entities) {
            Assert.notNull(entity.getId(), "The id of the entity must not be null");
            ids.add(entity.getId());
        }
        this.deleteAllById(ids, Instant.now());
    }

    /**
     * Marks the given entities as deleted in a batch which means it will create a single query.
     *
     * @param entities entities to be deleted. Must not be {@literal null}.
     */
    @Override
    @Transactional
    default void deleteAllInBatch(Iterable<T> entities) {
        this.deleteAll(entities);
    }

    /**
     * Marks the entities identified by the given ids as deleted using a single query.
     *
     * @param ids the ids of the entities to be deleted. Must not be {@literal null}.
     */
    @Override
    @Transactional
    default void deleteAllByIdInBatch(Iterable<ID> ids) {
        this.deleteAllById(ids);
    }

    /**
     * Marks all entities as deleted in a batch call.
     */
    @Override
    @Transactional
    default void deleteAllInBatch() {
        this.deleteAll();
    }

}
