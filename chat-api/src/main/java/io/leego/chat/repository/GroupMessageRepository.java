package io.leego.chat.repository;

import io.leego.chat.entity.GroupMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * @author Leego Yih
 */
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {

    List<GroupMessage> findBySenderAndEventTimeAfterOrderByEventTimeAscIdAsc(Long sender, Instant eventTime, Pageable pageable);

    List<GroupMessage> findBySenderAndEventTimeAndIdAfter(Long sender, Instant eventTime, Long id);

    @Query("select id, sender, sentTime, revokedTime from GroupMessage where id = ?1")
    Optional<GroupMessage> findByIdForRevoking(Long id);

    @Query("select id, sender, groupId, eventTime from GroupMessage where id = ?1")
    Optional<GroupMessage> findByIdForListing(Long id);

    @Nullable
    @Query("select status from GroupMessage where id = ?1 and sender = ?2")
    Short findStatusByIdAndSender(Long id, Long sender);

    /** Updates the {@code status} of the message with the given ID. */
    @Transactional
    @Modifying
    @Query("update GroupMessage set status = ?2 where id = ?1")
    int updateStatus(Long id, Short status);

    /** Sets the {@code revokedTime} of the message with the given ID. */
    @Modifying
    @Query("update GroupMessage set revokedTime = ?2, eventTime = ?2 where id = ?1 and revokedTime is null")
    int updateRevokedTime(Long id, Instant revokedTime);

}
