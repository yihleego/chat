package io.leego.chat.repository;

import io.leego.chat.entity.ContactRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

/**
 * @author Leego Yih
 */
public interface ContactRequestRepository extends JpaRepository<ContactRequest, Long> {

    boolean existsBySenderAndRecipientAndStatus(Long sender, Long recipient, Short status);

    List<ContactRequest> findByRecipient(Long recipient, Pageable pageable);

    List<ContactRequest> findByRecipientAndUpdatedTimeAfter(Long recipient, Instant updatedTime, Pageable pageable);

    /** Updates the status of the contact request with the given ID. */
    @Modifying
    @Query("update ContactRequest set status = ?3, updatedTime = ?4 where id = ?1 and status = ?2")
    int updateStatus(Long id, Short oldStatus, Short newStatus, Instant updatedTime);

}
