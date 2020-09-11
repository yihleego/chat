package io.leego.chat.manager.impl;

import io.leego.chat.constant.Constants;
import io.leego.chat.enums.MessageStatusEnum;
import io.leego.chat.manager.MessageManager;
import io.leego.chat.pojo.entity.Message;
import io.leego.chat.pojo.entity.MessageTimestamp;
import io.leego.chat.repository.MessageRepository;
import io.leego.chat.repository.MessageTimestampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Yihleego
 */
@Service
public class MessageManagerImpl implements MessageManager {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MessageTimestampRepository messageTimestampRepository;

    @Override
    public Message saveMessage(Message message) {
        return messageRepository.insert(message);
    }

    @Override
    public long updateMessageAsRead(String id, Long recipient) {
        Criteria criteria = Criteria
                .where(Constants.Message.ID).is(id)
                .and(Constants.Message.RECIPIENT).is(recipient)
                .and(Constants.Message.STATUS).is(MessageStatusEnum.UNREAD.getCode());
        Update update = Update.update(Constants.Message.STATUS, MessageStatusEnum.READ.getCode());
        return mongoTemplate.update(Message.class)
                .matching(criteria)
                .apply(update)
                .first()
                .getModifiedCount();
    }

    @Override
    public long updateMultiMessageAsRead(Long recipient, LocalDateTime endTime) {
        Criteria criteria = Criteria
                .where(Constants.Message.RECIPIENT).is(recipient)
                .and(Constants.Message.STATUS).is(MessageStatusEnum.UNREAD.getCode())
                .and(Constants.Message.TIME).lte(endTime);
        Update update = Update.update(Constants.Message.STATUS, MessageStatusEnum.READ.getCode());
        return mongoTemplate.update(Message.class)
                .matching(criteria)
                .apply(update)
                .all()
                .getModifiedCount();
    }

    @Override
    public long updateMultiMessageAsRead(Long recipient, LocalDateTime beginTime, LocalDateTime endTime) {
        Criteria criteria = Criteria
                .where(Constants.Message.RECIPIENT).is(recipient)
                .and(Constants.Message.STATUS).is(MessageStatusEnum.UNREAD.getCode())
                .and(Constants.Message.TIME).gte(beginTime).lte(endTime);
        Update update = Update.update(Constants.Message.STATUS, MessageStatusEnum.READ.getCode());
        return mongoTemplate.update(Message.class)
                .matching(criteria)
                .apply(update)
                .all()
                .getModifiedCount();
    }

    @Override
    public Message getMessage(String id, Long recipient) {
        return messageRepository.getByIdAndRecipient(id, recipient);
    }

    @Override
    public List<Message> listUnreadMessage(Long recipient, LocalDateTime time, int page, int size) {
        if (time != null) {
            return messageRepository.getByRecipientAndStatusAndTimeGreaterThanOrderByTimeAsc(
                    recipient,
                    MessageStatusEnum.UNREAD.getCode(),
                    time,
                    PageRequest.of(page - 1, size));
        } else {
            return messageRepository.getByRecipientAndStatusOrderByTimeAsc(
                    recipient,
                    MessageStatusEnum.UNREAD.getCode(),
                    PageRequest.of(page - 1, size));
        }
    }

    @Override
    public MessageTimestamp getMessageTimestampByUserId(Long userId) {
        return messageTimestampRepository.getByUserId(userId);
    }

    @Override
    public MessageTimestamp saveMessageTimestamp(MessageTimestamp messageTimestamp) {
        return messageTimestampRepository.save(messageTimestamp);
    }

}
