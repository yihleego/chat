package io.leego.chat.repository;

import io.leego.chat.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * @author Leego Yih
 */
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderAndEventTimeAfterOrderByEventTimeAscIdAsc(Long sender, Instant eventTime, Pageable pageable);

    List<Message> findBySenderAndEventTimeAndIdAfter(Long sender, Instant eventTime, Long id);

    List<Message> findByRecipientAndEventTimeAfterOrderByEventTimeAscIdAsc(Long recipient, Instant eventTime, Pageable pageable);

    List<Message> findByRecipientAndEventTimeAndIdAfter(Long recipient, Instant eventTime, Long id);

    @Query("select id, sender, sentTime, revokedTime from Message where id = ?1")
    Optional<Message> findByIdForRevoking(Long id);

    @Query("select id, sender, recipient, eventTime from Message where id = ?1")
    Optional<Message> findByIdForListing(Long id);

    /** Sets the {@code takenTime} of the messages with the given recipient and {@code eventTime} range. */
    @Modifying
    @Query("update Message set takenTime = ?3 where recipient = ?1 and eventTime >= ?2 and eventTime <= ?3 and takenTime is null and seenTime is null")
    int updateTakenTimeByRecipientAndEventTime(Long recipient, Instant begin, Instant end);

    /** Sets the {@code takenTime} of the message with the given ID and recipient. */
    @Modifying
    @Query("update Message set takenTime = ?3 where id = ?1 and recipient = ?2 and takenTime is null and seenTime is null")
    int updateTakenTime(Long id, Long recipient, Instant takenTime);

    /** Sets the {@code takenTime} of the messages with the given IDs and recipient. */
    @Modifying
    @Query("update Message set takenTime = ?3 where id in ?1 and recipient = ?2 and takenTime is null and seenTime is null")
    int updateTakenTimeInBatch(Long[] ids, Long recipient, Instant takenTime);

    /** Sets the {@code seenTime} of the message with the given ID and recipient. */
    @Modifying
    @Query("update Message set seenTime = ?3, eventTime = ?3 where id = ?1 and recipient = ?2 and seenTime is null")
    int updateSeenTime(Long id, Long recipient, Instant seenTime);

    /** Sets the {@code seenTime} of the messages with the given IDs and recipient. */
    @Modifying
    @Query("update Message set seenTime = ?3, eventTime = ?3 where id in ?1 and recipient = ?2 and seenTime is null")
    int updateSeenTimeInBatch(Long[] ids, Long recipient, Instant seenTime);

    /** Sets the {@code revokedTime} of the message with the given ID. */
    @Modifying
    @Query("update Message set revokedTime = ?2, eventTime = ?2 where id = ?1 and revokedTime is null")
    int updateRevokedTime(Long id, Instant revokedTime);

}
