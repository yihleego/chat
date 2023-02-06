package io.leego.chat.repository;

import io.leego.chat.entity.GroupMessageItem;
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
public interface GroupMessageItemRepository extends JpaRepository<GroupMessageItem, Long> {

    boolean existsByMessageIdAndRecipient(Long messageId, Long recipient);

    @Nullable
    GroupMessageItem findByMessageIdAndRecipient(Long messageId, Long recipient);

    List<GroupMessageItem> findByRecipientAndEventTimeAfterOrderByEventTimeAscMessageIdAsc(Long recipient, Instant eventTime, Pageable pageable);

    List<GroupMessageItem> findByRecipientAndEventTimeAndMessageIdAfter(Long recipient, Instant eventTime, Long messageId);

    List<GroupMessageItem> findByMessageId(Long messageId, Pageable pageable);

    List<GroupMessageItem> findByMessageIdAndEventTimeAfter(Long messageId, Instant eventTime, Pageable pageable);

    /** Generates message items for all group members. */
    @Modifying
    @Query("""
            insert into GroupMessageItem(recipient, messageId, eventTime)
            select userId, ?1, ?2 from GroupMember
            where groupId = ?3 and status = ?4 and deleted = 0""")
    int saveAll(Long messageId, Instant eventTime, Long groupId, Short status);

    /** Sets the {@code takenTime} of the messages with the given recipient and {@code eventTime} range. */
    @Modifying
    @Query("update GroupMessageItem set takenTime = ?3 where recipient = ?1 and eventTime >= ?2 and eventTime <= ?3 and takenTime is null and seenTime is null")
    int updateTakenTimeByRecipientAndEventTime(Long recipient, Instant begin, Instant end);

    /** Sets the {@code takenTime} of the message with the given message ID and recipient. */
    @Modifying
    @Query("update GroupMessageItem set takenTime = ?3 where messageId = ?1 and recipient = ?2 and takenTime is null and seenTime is null")
    int updateTakenTime(Long messageId, Long recipient, Instant takenTime);

    /** Sets the {@code takenTime} of the messages with the given message IDs and recipient. */
    @Modifying
    @Query("update GroupMessageItem set takenTime = ?3 where messageId in ?1 and recipient = ?2 and takenTime is null and seenTime is null")
    int updateTakenTimeInBatch(Long[] messageIds, Long recipient, Instant takenTime);

    /** Sets the {@code seenTime} of the message with the given message ID and recipient. */
    @Modifying
    @Query("update GroupMessageItem set seenTime = ?3, eventTime = ?3 where messageId = ?1 and recipient = ?2 and seenTime is null")
    int updateSeenTime(Long messageId, Long recipient, Instant seenTime);

    /** Sets the {@code seenTime} of the messages with the given message IDs and recipient. */
    @Modifying
    @Query("update GroupMessageItem set seenTime = ?3, eventTime = ?3 where messageId in ?1 and recipient = ?2 and seenTime is null")
    int updateSeenTimeInBatch(Long[] messageIds, Long recipient, Instant seenTime);

}
