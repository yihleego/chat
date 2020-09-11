package io.leego.chat.service.impl;

import io.leego.chat.Result;
import io.leego.chat.enums.MessageStatusEnum;
import io.leego.chat.manager.MessageManager;
import io.leego.chat.pojo.dto.MessageSaveDTO;
import io.leego.chat.pojo.entity.Message;
import io.leego.chat.pojo.entity.MessageTimestamp;
import io.leego.chat.service.MessageService;
import io.leego.chat.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * @author Yihleego
 */
@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageManager messageManager;

    @Override
    public Result<Message> getMessage(String id) {
        Message message = messageManager.getMessage(id, getUserId());
        if (message == null) {
            return Result.buildFailure("No messages");
        }
        return Result.buildSuccess(message);
    }

    @Override
    public Result<Message> saveMessage(MessageSaveDTO save) {
        if (save.getContent() == null) {
            return Result.buildFailure("Message content cannot be empty");
        }
        if (save.getContent().length() > 2048) {
            return Result.buildFailure("Message content is too long");
        }
        Message message = new Message();
        message.setSender(getUserId());
        message.setRecipient(save.getRecipient());
        message.setContent(save.getContent());
        message.setTime(LocalDateTime.now());
        message.setType(save.getType());
        message.setStatus(MessageStatusEnum.UNREAD.getCode());
        return Result.buildSuccess(messageManager.saveMessage(message));
    }

    @Override
    public Result<Void> markMessage(String id) {
        messageManager.updateMessageAsRead(id, getUserId());
        return Result.buildSuccess();
    }

    @Override
    public Result<List<Message>> listUnreadMessage(String anchor) {
        Long userId = getUserId();
        MessageTimestamp timestamp = messageManager.getMessageTimestampByUserId(userId);
        LocalDateTime lastTime = timestamp != null ? timestamp.getTime() : null;
        if (anchor != null) {
            Message lastMessage = messageManager.getMessage(anchor, userId);
            if (lastMessage == null) {
                return Result.buildSuccess(Collections.emptyList());
            }
            lastTime = lastMessage.getTime();
            if (timestamp == null) {
                messageManager.updateMultiMessageAsRead(userId, lastMessage.getTime());
                messageManager.saveMessageTimestamp(new MessageTimestamp(null, userId, lastMessage.getTime()));
            } else if (timestamp.getTime().compareTo(lastMessage.getTime()) < 0) {
                messageManager.updateMultiMessageAsRead(userId, timestamp.getTime(), lastMessage.getTime());
                messageManager.saveMessageTimestamp(new MessageTimestamp(timestamp.getId(), userId, lastMessage.getTime()));
            }
        }
        List<Message> messages = messageManager.listUnreadMessage(userId, lastTime, 1, 100);
        if (messages.isEmpty()) {
            return Result.buildSuccess(Collections.emptyList());
        }
        return Result.buildSuccess(messages);
    }

    private Long getUserId() {
        return UserUtils.getUserId();
    }

}
