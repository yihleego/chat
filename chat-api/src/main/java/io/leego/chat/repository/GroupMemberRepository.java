package io.leego.chat.repository;

import io.leego.chat.entity.GroupMember;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.List;

/**
 * @author Leego Yih
 */
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long>, DeletableRepository<GroupMember, Long> {

    @Nullable
    GroupMember findByGroupIdAndUserId(Long groupId, Long userId);

    List<GroupMember> findByUserId(Long userId, Pageable pageable);

    List<GroupMember> findByUserIdAndUpdatedTimeAfter(Long userId, Instant updatedTime, Pageable pageable);

    List<GroupMember> findByGroupId(Long groupId, Pageable pageable);

    List<GroupMember> findByGroupIdAndUpdatedTimeAfter(Long groupId, Instant updatedTime, Pageable pageable);

    @Query("select userId from GroupMember where groupId = ?1 and status = ?2")
    List<Long> findUserIdByGroupIdAndStatus(Long groupId, Short status);

    /** Updates the status of the group member with the given ID. */
    @Modifying
    @Query("update GroupMember set status = ?3, updatedTime = ?4 where id = ?1 and status = ?2")
    int updateStatus(Long id, Short oldStatus, Short newStatus, Instant updatedTime);

}