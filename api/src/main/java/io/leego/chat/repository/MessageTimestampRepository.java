package io.leego.chat.repository;

import io.leego.chat.pojo.entity.MessageTimestamp;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Yihleego
 */
public interface MessageTimestampRepository extends MongoRepository<MessageTimestamp, String> {

    MessageTimestamp getByUserId(long userId);

}
