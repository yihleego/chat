package io.leego.chat.service.impl;

import io.leego.chat.config.ChatProperties;
import io.leego.chat.constant.ClientType;
import io.leego.chat.constant.DeviceType;
import io.leego.chat.constant.UserType;
import io.leego.chat.dto.MessageCreateDTO;
import io.leego.chat.entity.Message;
import io.leego.chat.entity.MessageStamp;
import io.leego.chat.exception.ForbiddenException;
import io.leego.chat.exception.NotFoundException;
import io.leego.chat.manager.ContactManager;
import io.leego.chat.repository.MessageRepository;
import io.leego.chat.repository.MessageStampRepository;
import io.leego.chat.service.MessageService;
import io.leego.chat.vo.MessagePrimeVO;
import io.leego.chat.vo.MessageVO;
import io.leego.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * @author Leego Yih
 */
@Service
public class MessageServiceImpl implements MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    private final MessageRepository messageRepository;
    private final MessageStampRepository messageStampRepository;
    private final ContactManager contactManager;
    private final ChatProperties properties;

    public MessageServiceImpl(
            MessageRepository messageRepository,
            MessageStampRepository messageStampRepository,
            ContactManager contactManager,
            ChatProperties properties) {
        this.messageRepository = messageRepository;
        this.messageStampRepository = messageStampRepository;
        this.contactManager = contactManager;
        this.properties = properties;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public MessagePrimeVO createMessage(MessageCreateDTO dto) {
        Long sender = getUserId();
        Long recipient = dto.getRecipient();
        if (!contactManager.isContact(sender, recipient)) {
            throw new ForbiddenException("No contact [%d] -> [%d]".formatted(sender, recipient));
        }
        Instant now = Instant.now();
        Message message = new Message(
                sender, recipient, dto.getType(), dto.getContent(),
                now, now, null, null, null);
        messageRepository.save(message);
        return new MessagePrimeVO(message.getId(), message.getSentTime());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void takeMessages(Long[] ids) {
        if (ids.length == 1) {
            messageRepository.updateTakenTime(ids[0], getUserId(), Instant.now());
        } else {
            messageRepository.updateTakenTimeInBatch(ids, getUserId(), Instant.now());
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void readMessages(Long[] ids) {
        if (ids.length == 1) {
            messageRepository.updateSeenTime(ids[0], getUserId(), Instant.now());
        } else {
            messageRepository.updateSeenTimeInBatch(ids, getUserId(), Instant.now());
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void revokeMessage(Long id) {
        Long userId = getUserId();
        Message message = messageRepository.findByIdForRevoking(id)
                .filter(o -> o.getSender().equals(userId))
                .orElseThrow(() -> new NotFoundException("Revoke failed, cannot find message [%d] for user [%d]".formatted(id, userId)));
        if (message.getRevokedTime() != null) {
            logger.warn("Message [{}] has been revoked", id);
            return;
        }
        Instant now = Instant.now();
        if (message.getSentTime().isBefore(now.minus(properties.getMessage().getRevokeTimeout()))) {
            logger.warn("Message [{}] is too old to be revoked", id);
            throw new ForbiddenException();
        }
        messageRepository.updateRevokedTime(id, now);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.SUPPORTS, readOnly = true)
    public MessageVO getMessage(Long id) {
        Long userId = getUserId();
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cannot find message [%d] for user [%d]".formatted(id, userId)));
        if (!userId.equals(message.getRecipient()) && !userId.equals(message.getSender())) {
            throw new NotFoundException("The message [%d] does not belong to the user [%d]".formatted(id, userId));
        }
        if (message.getEventTime().isBefore(Instant.now().minus(properties.getMessage().getObtainTimeout()))) {
            throw new NotFoundException("The message [%d] has expired".formatted(id));
        }
        return toVO(message);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<MessageVO> listMessages(Long id, Short type) {
        Long userId = getUserId();
        Long deviceId = getDeviceId();
        UserType userType = UserType.get(type);
        DeviceType deviceType = DeviceType.get(getDeviceType());
        ClientType clientType = ClientType.get(getClientType());
        if (userType == null || deviceId == null || deviceType == null || clientType == null
                || deviceType == DeviceType.UNKNOWN || clientType == ClientType.UNKNOWN || !clientType.isSyncable()) {
            throw new ForbiddenException("Invalid type [%s] or device [%s] or client [%s]".formatted(userType, deviceType, clientType));
        }
        logger.debug("Fetch messages, messageId={}, userId={}, userType={}, device={}-{}, client={}", id, userId, userType, deviceType, deviceId, clientType);
        // Find the message if the given message id exists
        Message message = null;
        if (id != null) {
            message = messageRepository.findByIdForListing(id)
                    .filter(o -> o.getRecipient().equals(userId) || o.getSender().equals(userId))
                    .orElseThrow(NotFoundException::new);
        }
        // Guarantee that the message stamp exists
        MessageStamp stamp = messageStampRepository.findByUserIdAndUserTypeAndDeviceIdAndDeviceTypeAndClientType(
                userId, type, deviceId, deviceType.getCode(), clientType.getCode());
        if (stamp == null) {
            // Allow to get messages for the specified time range if the user logs into the client for the first time
            Instant lt = Instant.now().minus(properties.getMessage().getHistoryTimeout());
            stamp = new MessageStamp(userId, type, deviceId, deviceType.getCode(), clientType.getCode(), 0L, lt);
            messageStampRepository.save(stamp);
            logger.info("Create a new message stamp: {}", stamp);
        }
        // Choose the more recent time as the criteria
        Instant lastTime = stamp.getLastTime();
        if (message != null) {
            Instant eventTime = message.getEventTime();
            if (lastTime.isBefore(eventTime) || (lastTime.equals(eventTime) && stamp.getMessageId() < message.getId())) {
                if (userType == UserType.RECIPIENT) {
                    // Mark messages as taken
                    messageRepository.updateTakenTimeByRecipientAndEventTime(userId, lastTime, eventTime);
                }
                // Renew message stamp
                messageStampRepository.updateLastTime(stamp.getId(), message.getId(), eventTime);
                // Use the event time of the message
                lastTime = eventTime;
            }
        }
        /*
         * Query messages cannot be sorted by id only.
         * In a distributed system, there is no guarantee that the larger the event time is, the larger the id will be.
         * For example:
         * ┌──────┬─────────────────────┐
         * │ id ▲2│ event_time        ▲1│ <- ORDER BY event_time ASC, id ASC
         * ├──────┼─────────────────────┤
         * │ 200  │ 1999-12-31 23:59:59 │ <- The event time with id [200] is less than the event time with ID [100].
         * ├──────┼─────────────────────┤
         * │ 201  │ 1999-12-31 23:59:59 │
         * ├──────┼─────────────────────┤
         * │ 100  │ 2000-01-01 00:00:00 │
         * ├──────┼─────────────────────┤
         * │ 101  │ 2000-01-01 00:00:00 │
         * └──────┴─────────────────────┘
         */
        int fetchSize = properties.getMessage().getFetchSize();
        List<Message> messages = userType == UserType.SENDER
                ? messageRepository.findBySenderAndEventTimeAfterOrderByEventTimeAscIdAsc(userId, lastTime, Pageable.ofSize(fetchSize))
                : messageRepository.findByRecipientAndEventTimeAfterOrderByEventTimeAscIdAsc(userId, lastTime, Pageable.ofSize(fetchSize));
        int size = messages.size();
        if (size == 0) {
            return Collections.emptyList();
        }
        // Return messages directly if the actual size is less than the expected size
        if (size < fetchSize) {
            return messages.stream().map(this::toVO).toList();
        }
        // Guarantee that all messages with the event same time are not lost
        Message first = messages.get(0);
        Message last = messages.get(size - 1);
        if (first.getEventTime().equals(last.getEventTime())) {
            /*
             * Append additional messages if all messages have the same event time.
             * For example:
             * ┌──────┬─────────────────────┐
             * │ id ▲2│ event_time        ▲1│ <- ORDER BY event_time ASC, id ASC LIMIT 0, 10
             * ├──────┼─────────────────────┤
             * │ 99   │ 1999-12-31 23:59:59 │ <- Assume the given ID is [99] and the time is [1999-12-31 23:59:59]
             * ├──────┼─────────────────────┤
             * │ 100  │ 2000-01-01 00:00:00 │ <- Query starts here
             * ├──────┼─────────────────────┤
             * │ ...  │ 2000-01-01 00:00:00 │ <- Omit 8 records
             * ├──────┼─────────────────────┤
             * │ 109  │ 2000-01-01 00:00:00 │ <- Query ends here, 10 records
             * ├──────┼─────────────────────┤
             * │ ...  │ 2000-01-01 00:00:00 │ <- These records with the same time will be additionally added to the query results
             * ├──────┼─────────────────────┤
             * │ 201  │ 2000-11-11 11:11:11 │ <- Cannot reach here
             * └──────┴─────────────────────┘
             */
            logger.error("Suspicious messages [{} -> {}] found, please let developers to check the data.", first.getId(), last.getId());
            List<Message> ex = userType == UserType.SENDER
                    ? messageRepository.findBySenderAndEventTimeAndIdAfter(userId, last.getEventTime(), last.getId())
                    : messageRepository.findByRecipientAndEventTimeAndIdAfter(userId, last.getEventTime(), last.getId());
            if (!ex.isEmpty()) {
                messages.addAll(ex);
            }
            return messages.stream().map(this::toVO).toList();
        } else {
            /*
             * Remove the last few messages with the same event time.
             * For example:
             * ┌──────┬─────────────────────┐
             * │ id ▲2│ event_time        ▲1│ <- ORDER BY event_time ASC, id ASC LIMIT 0, 10
             * ├──────┼─────────────────────┤
             * │ 99   │ 1999-12-31 23:59:59 │ <- Assume the given ID is [99] and the last time is [1999-12-31 23:59:59]
             * ├──────┼─────────────────────┤
             * │ 100  │ 2000-01-01 00:00:00 │ <- Query starts here
             * ├──────┼─────────────────────┤
             * │ ...  │ 2000-01-01 00:00:00 │ <- Omit 8 records
             * ├──────┼─────────────────────┤
             * │ 109  │ 2000-11-11 11:11:11 │ <- Query ends here, this will be removed from the query results, and the next query will start here
             * ├──────┼─────────────────────┤
             * │ ...  │ 2000-11-11 11:11:11 │ <- Guarantee these messages are not lost in the next query
             * └──────┴─────────────────────┘
             */
            return messages.stream()
                    .filter(o -> o.getEventTime().isBefore(last.getEventTime()))
                    .map(this::toVO)
                    .toList();
        }
    }

    MessageVO toVO(Message o) {
        // Hide the content if it has been revoked
        boolean seen = o.getSeenTime() != null;
        boolean taken = seen || o.getTakenTime() != null;
        boolean revoked = o.getRevokedTime() != null;
        return new MessageVO(
                o.getId(), o.getSender(), o.getRecipient(),
                revoked ? null : o.getType(),
                revoked ? null : o.getContent(),
                taken, seen, revoked,
                o.getSentTime());
    }

    Long getUserId() {
        return SecurityUtils.getUserId();
    }

    Long getDeviceId() {
        return SecurityUtils.getDeviceId();
    }

    Short getDeviceType() {
        return SecurityUtils.getDeviceType();
    }

    Short getClientType() {
        return SecurityUtils.getClientType();
    }
}
