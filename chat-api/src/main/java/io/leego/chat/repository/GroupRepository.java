package io.leego.chat.repository;

import io.leego.chat.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

import java.time.Instant;

/**
 * @author Leego Yih
 */
public interface GroupRepository extends JpaRepository<Group, Long> {

    int countByOwnerAndStatus(Long owner, Short status);

    @Nullable
    Group findByIdAndStatus(Long id, Short status);

    /** Updates the status of the group with the given ID. */
    @Modifying
    @Query("update Group set status = ?3, updatedTime = ?4 where id = ?1 and status = ?2")
    int updateStatus(Long id, Short oldStatus, Short newStatus, Instant updatedTime);

}