package io.leego.chat.repository;

import io.leego.chat.entity.Contact;
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
public interface ContactRepository extends JpaRepository<Contact, Long>, DeletableRepository<Contact, Long> {

    boolean existsBySenderAndRecipientAndStatus(Long sender, Long recipient, Short status);

    @Nullable
    Contact findBySenderAndRecipient(Long sender, Long recipient);

    List<Contact> findBySender(Long sender, Pageable pageable);

    List<Contact> findBySenderAndUpdatedTimeAfter(Long sender, Instant updatedTime, Pageable pageable);

    /** Updates the status of the contact with the given ID. */
    @Modifying
    @Query("update Contact set status = ?3, updatedTime = ?4 where id = ?1 and status = ?2")
    int updateStatus(Long id, Short oldStatus, Short newStatus, Instant updatedTime);

}
