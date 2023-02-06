package io.leego.chat.service.impl;

import io.leego.chat.config.ChatProperties;
import io.leego.chat.constant.ClientType;
import io.leego.chat.constant.DeviceType;
import io.leego.chat.constant.GroupMessageStatus;
import io.leego.chat.constant.MemberStatus;
import io.leego.chat.constant.UserType;
import io.leego.chat.dto.GroupMessageCreateDTO;
import io.leego.chat.entity.BaseEntity;
import io.leego.chat.entity.GroupMessage;
import io.leego.chat.entity.GroupMessageItem;
import io.leego.chat.entity.GroupMessageStamp;
import io.leego.chat.exception.ForbiddenException;
import io.leego.chat.exception.NotFoundException;
import io.leego.chat.manager.GroupManager;
import io.leego.chat.repository.GroupMessageItemRepository;
import io.leego.chat.repository.GroupMessageRepository;
import io.leego.chat.repository.GroupMessageStampRepository;
import io.leego.chat.service.GroupMessageService;
import io.leego.chat.vo.GroupMessagePrimeVO;
import io.leego.chat.vo.GroupMessageStateVO;
import io.leego.chat.vo.GroupMessageVO;
import io.leego.chat.vo.MentionVO;
import io.leego.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Leego Yih
 */
@Service
public class GroupMessageServiceImpl implements GroupMessageService {
    private static final Logger logger = LoggerFactory.getLogger(GroupMessageServiceImpl.class);
    private final GroupMessageRepository groupMessageRepository;
    private final GroupMessageItemRepository groupMessageItemRepository;
    private final GroupMessageStampRepository groupMessageStampRepository;
    private final GroupManager groupManager;
    private final ChatProperties properties;

    public GroupMessageServiceImpl(
            GroupMessageRepository groupMessageRepository,
            GroupMessageItemRepository groupMessageItemRepository,
            GroupMessageStampRepository groupMessageStampRepository,
            GroupManager groupManager,
            ChatProperties properties) {
        this.groupMessageRepository = groupMessageRepository;
        this.groupMessageItemRepository = groupMessageItemRepository;
        this.groupMessageStampRepository = groupMessageStampRepository;
        this.groupManager = groupManager;
        this.properties = properties;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public GroupMessagePrimeVO createGroupMessage(GroupMessageCreateDTO dto) {
        Long sender = getUserId();
        Long groupId = dto.getGroupId();
        if (!groupManager.isMember(groupId, sender)) {
            throw new ForbiddenException("Not member [%d] -> [%d]".formatted(sender, groupId));
        }
        Instant now = Instant.now();
        GroupMessage.Mention[] mentions = null;
        if (!ObjectUtils.isEmpty(dto.getMentions())) {
            mentions = Arrays.stream(dto.getMentions())
                    .map(o -> new GroupMessage.Mention(o.getUserId(), o.getIndex()))
                    .toArray(GroupMessage.Mention[]::new);
        }
        GroupMessage message = new GroupMessage(
                groupId, sender, dto.getType(), dto.getContent(), mentions,
                GroupMessageStatus.READY.getCode(),
                now, now, null);
        groupMessageRepository.save(message);
        // Generate message items for all members
        groupMessageItemRepository.saveAll(message.getId(), message.getEventTime(), message.getGroupId(), MemberStatus.JOINED.getCode());
        return new GroupMessagePrimeVO(message.getId(), message.getSentTime(), message.getStatus());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void takeGroupMessages(Long[] ids) {
        if (ids.length == 1) {
            groupMessageItemRepository.updateTakenTime(ids[0], getUserId(), Instant.now());
        } else {
            groupMessageItemRepository.updateTakenTimeInBatch(ids, getUserId(), Instant.now());
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void readGroupMessages(Long[] ids) {
        if (ids.length == 1) {
            groupMessageItemRepository.updateSeenTime(ids[0], getUserId(), Instant.now());
        } else {
            groupMessageItemRepository.updateSeenTimeInBatch(ids, getUserId(), Instant.now());
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void revokeGroupMessage(Long id) {
        Long userId = getUserId();
        GroupMessage message = groupMessageRepository.findByIdForRevoking(id)
                .filter(o -> o.getSender().equals(userId))
                .orElseThrow(() -> new NotFoundException("Revoke failed, cannot find group message [%d] for user [%d]".formatted(id, userId)));
        if (message.getRevokedTime() != null) {
            logger.warn("Group message [{}] has been revoked", id);
            return;
        }
        Instant now = Instant.now();
        if (message.getSentTime().isBefore(now.minus(properties.getMessage().getRevokeTimeout()))) {
            logger.warn("Group message [{}] is too old to be revoked", id);
            throw new ForbiddenException();
        }
        groupMessageRepository.updateRevokedTime(id, now);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.SUPPORTS, readOnly = true)
    public GroupMessageVO getGroupMessage(Long id) {
        Long userId = getUserId();
        GroupMessage message = groupMessageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cannot find message [%d] for user [%d]".formatted(id, userId)));
        if (message.getEventTime().isBefore(Instant.now().minus(properties.getMessage().getObtainTimeout()))) {
            throw new NotFoundException("The message [%d] has expired".formatted(id));
        }
        if (userId.equals(message.getSender())) {
            return toVO(message);// for sender
        }
        GroupMessageItem item = groupMessageItemRepository.findByMessageIdAndRecipient(id, userId);
        if (item != null) {
            return toVO(message, item);// for recipient
        }
        throw new NotFoundException("The message [%d] does not belong to the user [%d]".formatted(id, userId));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.SUPPORTS, readOnly = true)
    public GroupMessageStateVO getGroupMessageState(Long id, Instant lastTime) {
        Long userId = getUserId();
        Short status = groupMessageRepository.findStatusByIdAndSender(id, userId);
        if (status == null) {
            throw new NotFoundException("Cannot find message [%d] for user [%d]".formatted(id, userId));
        }
        if (status == GroupMessageStatus.SENDING.getCode()) {
            throw new NotFoundException("The message [%d] is not ready yet".formatted(id));
        }
        Pageable pageable = Pageable.ofSize(properties.getMessage().getFetchSize());
        List<GroupMessageItem> items = lastTime == null
                ? groupMessageItemRepository.findByMessageId(id, pageable)
                : groupMessageItemRepository.findByMessageIdAndEventTimeAfter(id, lastTime, pageable);
        if (items.isEmpty()) {
            return new GroupMessageStateVO(null, null, null);
        }
        List<Long> taken = new ArrayList<>(items.size());
        List<Long> seen = new ArrayList<>(items.size());
        Instant maxTime = items.get(0).getEventTime();
        for (GroupMessageItem o : items) {
            if (o.getSeenTime() != null) {
                taken.add(o.getRecipient());
                seen.add(o.getRecipient());
            } else if (o.getTakenTime() != null) {
                taken.add(o.getRecipient());
            }
            if (o.getEventTime().isAfter(maxTime)) {
                maxTime = o.getEventTime();
            }
        }
        return new GroupMessageStateVO(taken, seen, maxTime);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<GroupMessageVO> listGroupMessages(Long id, Short type) {
        Long userId = getUserId();
        Long deviceId = getDeviceId();
        UserType userType = UserType.get(type);
        DeviceType deviceType = DeviceType.get(getDeviceType());
        ClientType clientType = ClientType.get(getClientType());
        if (userType == null || deviceId == null || deviceType == null || clientType == null
                || deviceType == DeviceType.UNKNOWN || clientType == ClientType.UNKNOWN || !clientType.isSyncable()) {
            throw new ForbiddenException("Invalid type [%s] or device [%s] or client [%s]".formatted(userType, deviceType, clientType));
        }
        logger.debug("Fetch group messages, messageId={}, userId={}, userType={}, device={}-{}, client={}", id, userId, userType, deviceType, deviceId, clientType);
        // Find the group message if the given message id exists
        GroupMessage message = null;
        if (id != null) {
            message = groupMessageRepository.findByIdForListing(id)
                    .filter(o -> userType == UserType.SENDER
                            ? o.getSender().equals(userId)
                            : groupMessageItemRepository.existsByMessageIdAndRecipient(id, userId))
                    .orElseThrow(NotFoundException::new);
        }
        // Guarantee that the message stamp exists
        GroupMessageStamp stamp = groupMessageStampRepository.findByUserIdAndUserTypeAndDeviceIdAndDeviceTypeAndClientType(
                userId, type, deviceId, deviceType.getCode(), clientType.getCode());
        if (stamp == null) {
            // Allow to get messages for the specified time range if the user logs into the client for the first time
            Instant lt = Instant.now().minus(properties.getMessage().getHistoryTimeout());
            stamp = new GroupMessageStamp(userId, type, deviceId, deviceType.getCode(), clientType.getCode(), 0L, lt);
            groupMessageStampRepository.save(stamp);
            logger.info("Create a new group message stamp: {}", stamp);
        }
        return userType == UserType.SENDER
                ? listGroupMessagesForSender(userId, message, stamp)
                : listGroupMessagesForRecipient(userId, message, stamp);
    }

    public List<GroupMessageVO> listGroupMessagesForSender(Long userId, GroupMessage message, GroupMessageStamp stamp) {
        // Choose the more recent time as the criteria
        Instant lastTime = stamp.getLastTime();
        if (message != null) {
            Instant eventTime = message.getEventTime();
            if (lastTime.isBefore(eventTime) || (lastTime.equals(eventTime) && stamp.getMessageId() < message.getId())) {
                // Renew message stamp
                groupMessageStampRepository.updateLastTime(stamp.getId(), message.getId(), eventTime);
                // Use the event time of the message
                lastTime = eventTime;
            }
        }
        // Query the specified number of messages
        int fetchSize = properties.getMessage().getFetchSize();
        List<GroupMessage> messages = groupMessageRepository.findBySenderAndEventTimeAfterOrderByEventTimeAscIdAsc(userId, lastTime, Pageable.ofSize(fetchSize));
        int size = messages.size();
        if (size == 0) {
            return Collections.emptyList();
        }
        // Return messages directly if the actual size is less than the expected size
        if (size < fetchSize) {
            return messages.stream().map(this::toVO).toList();
        }
        // Guarantee that all messages with the event same time are not lost
        GroupMessage first = messages.get(0);
        GroupMessage last = messages.get(size - 1);
        if (first.getEventTime().equals(last.getEventTime())) {
            // Append additional messages if all messages have the same event time
            logger.error("Suspicious messages [{}->{}] found, please let developers to check the data.", first.getId(), last.getId());
            List<GroupMessage> ex = groupMessageRepository.findBySenderAndEventTimeAndIdAfter(userId, last.getEventTime(), last.getId());
            if (!ex.isEmpty()) {
                messages.addAll(ex);
            }
            return messages.stream().map(this::toVO).toList();
        } else {
            // Remove the last few messages with the same event time
            return messages.stream()
                    .filter(o -> o.getEventTime().isBefore(last.getEventTime()))
                    .map(this::toVO)
                    .toList();
        }
    }

    public List<GroupMessageVO> listGroupMessagesForRecipient(Long userId, GroupMessage message, GroupMessageStamp stamp) {
        // Choose the more recent time as the criteria
        Instant lastTime = stamp.getLastTime();
        if (message != null) {
            Instant eventTime = message.getEventTime();
            if (lastTime.isBefore(eventTime) || (lastTime.equals(eventTime) && stamp.getMessageId() < message.getId())) {
                // Mark messages as taken
                groupMessageItemRepository.updateTakenTimeByRecipientAndEventTime(userId, lastTime, eventTime);
                // Renew message stamp
                groupMessageStampRepository.updateLastTime(stamp.getId(), message.getId(), eventTime);
                // Use the event time of the message
                lastTime = eventTime;
            }
        }
        // Query the specified number of message items
        int fetchSize = properties.getMessage().getFetchSize();
        List<GroupMessageItem> items = groupMessageItemRepository.findByRecipientAndEventTimeAfterOrderByEventTimeAscMessageIdAsc(userId, lastTime, Pageable.ofSize(fetchSize));
        int size = items.size();
        if (size == 0) {
            return Collections.emptyList();
        }
        // Return messages directly if the actual size is less than the expected size
        if (size < fetchSize) {
            return toVOs(items, null);
        }
        // Guarantee that all messages with the event same time are not lost
        GroupMessageItem first = items.get(0);
        GroupMessageItem last = items.get(size - 1);
        if (first.getEventTime().equals(last.getEventTime())) {
            // Append additional messages if all messages have the same event time
            logger.error("Suspicious messages [{}->{}] found, please let developers to check the data.", first.getMessageId(), last.getMessageId());
            List<GroupMessageItem> ex = groupMessageItemRepository.findByRecipientAndEventTimeAndMessageIdAfter(userId, last.getEventTime(), last.getMessageId());
            if (!ex.isEmpty()) {
                items.addAll(ex);
            }
            return toVOs(items, null);
        } else {
            // Remove the last few messages with the same event time
            return toVOs(items, last.getEventTime());
        }
    }

    GroupMessageVO toVO(GroupMessage o) {
        return toVO(o, null);
    }

    GroupMessageVO toVO(GroupMessage o, GroupMessageItem item) {
        // Hide the content if it has been revoked
        boolean seen = false;
        boolean taken = false;
        boolean revoked = o.getRevokedTime() != null;
        if (item != null) {
            seen = item.getSeenTime() != null;
            taken = seen || item.getTakenTime() != null;
        }
        return new GroupMessageVO(
                o.getId(), o.getGroupId(), o.getSender(),
                revoked ? null : o.getType(),
                revoked ? null : o.getContent(),
                revoked ? null : toMentionVOs(o.getMentions()),
                taken, seen, revoked,
                o.getSentTime(),
                o.getStatus());
    }

    List<GroupMessageVO> toVOs(List<GroupMessageItem> items, Instant exclusive) {
        Map<Long, GroupMessageItem> itemMap = new HashMap<>(items.size());
        List<Long> messageIds = new ArrayList<>(items.size());
        for (GroupMessageItem item : items) {
            if (exclusive == null || item.getEventTime().isBefore(exclusive)) {
                messageIds.add(item.getMessageId());
                itemMap.put(item.getMessageId(), item);
            }
        }
        List<GroupMessage> messages = groupMessageRepository.findAllById(messageIds);
        return messages.stream()
                .sorted(Comparator.comparing(GroupMessage::getEventTime).thenComparingLong(BaseEntity::getId))
                .map(o -> toVO(o, itemMap.get(o.getId())))
                .toList();
    }

    MentionVO[] toMentionVOs(GroupMessage.Mention[] mentions) {
        if (ObjectUtils.isEmpty(mentions)) {
            return null;
        }
        return Arrays.stream(mentions)
                .map(o -> new MentionVO(o.getUserId(), o.getIndex()))
                .toArray(MentionVO[]::new);
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
