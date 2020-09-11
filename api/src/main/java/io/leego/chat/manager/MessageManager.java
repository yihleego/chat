package io.leego.chat.manager;

import io.leego.chat.pojo.entity.Message;
import io.leego.chat.pojo.entity.MessageTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Yihleego
 */
public interface MessageManager {

    Message saveMessage(Message message);

    long updateMessageAsRead(String id, Long recipient);

    long updateMultiMessageAsRead(Long recipient, LocalDateTime endTime);

    long updateMultiMessageAsRead(Long recipient, LocalDateTime beginTime, LocalDateTime endTime);

    Message getMessage(String id, Long recipient);

    List<Message> listUnreadMessage(Long recipient, LocalDateTime time, int page, int size);

    MessageTimestamp saveMessageTimestamp(MessageTimestamp messageTimestamp);

    MessageTimestamp getMessageTimestampByUserId(Long userId);

}
