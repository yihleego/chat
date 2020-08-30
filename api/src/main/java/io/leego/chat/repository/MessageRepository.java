package io.leego.chat.repository;

import io.leego.chat.pojo.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Yihleego
 */
public interface MessageRepository extends MongoRepository<Message, String> {

    Message getByIdAndRecipient(String id, long recipient);

    List<Message> getByRecipientAndStatusOrderByTimeAsc(long recipient, short status, Pageable pageable);

    List<Message> getByRecipientAndStatusAndTimeGreaterThanOrderByTimeAsc(long recipient, short status, @Nullable LocalDateTime time, Pageable pageable);

}
