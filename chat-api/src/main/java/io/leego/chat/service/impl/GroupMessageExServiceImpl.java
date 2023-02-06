package io.leego.chat.service.impl;

import io.leego.chat.config.ChatProperties;
import io.leego.chat.constant.GroupMessageStatus;
import io.leego.chat.constant.MemberStatus;
import io.leego.chat.dto.GroupMessageCreateDTO;
import io.leego.chat.entity.GroupMessage;
import io.leego.chat.entity.GroupMessageItem;
import io.leego.chat.exception.ForbiddenException;
import io.leego.chat.manager.GroupManager;
import io.leego.chat.repository.GroupMemberRepository;
import io.leego.chat.repository.GroupMessageItemRepository;
import io.leego.chat.repository.GroupMessageRepository;
import io.leego.chat.repository.GroupMessageStampRepository;
import io.leego.chat.vo.GroupMessagePrimeVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Leego Yih
 * @deprecated For sharding only.
 */
@Deprecated
public class GroupMessageExServiceImpl extends GroupMessageServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(GroupMessageExServiceImpl.class);
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMessageRepository groupMessageRepository;
    private final GroupManager groupManager;
    private final ChatProperties properties;
    private final JdbcTemplate jdbcTemplate;
    private final ExecutorService executorService;

    public GroupMessageExServiceImpl(
            GroupMemberRepository groupMemberRepository,
            GroupMessageRepository groupMessageRepository,
            GroupMessageItemRepository groupMessageItemRepository,
            GroupMessageStampRepository groupMessageStampRepository,
            GroupManager groupManager,
            ChatProperties properties,
            JdbcTemplate jdbcTemplate) {
        super(groupMessageRepository, groupMessageItemRepository, groupMessageStampRepository, groupManager, properties);
        this.groupMemberRepository = groupMemberRepository;
        this.groupMessageRepository = groupMessageRepository;
        this.groupManager = groupManager;
        this.properties = properties;
        this.jdbcTemplate = jdbcTemplate;
        this.executorService = Executors.newFixedThreadPool(Math.max(1, Runtime.getRuntime().availableProcessors() * 2));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public GroupMessagePrimeVO createGroupMessage(GroupMessageCreateDTO dto) {
        Long sender = getUserId();
        Long groupId = dto.getGroupId();
        if (!groupManager.isMember(groupId, sender)) {
            throw new ForbiddenException("Not member");
        }
        Instant now = Instant.now();
        // Count the number of members, excluding self
        int memberSize = groupManager.countMembers(dto.getGroupId()) - 1;
        // Asynchronously persist the message if there are too many members
        int batchSize = properties.getMessage().getBatchSize();
        boolean async = memberSize > batchSize;
        GroupMessage.Mention[] mentions = null;
        if (!ObjectUtils.isEmpty(dto.getMentions())) {
            mentions = Arrays.stream(dto.getMentions())
                    .map(o -> new GroupMessage.Mention(o.getUserId(), o.getIndex()))
                    .toArray(GroupMessage.Mention[]::new);
        }
        GroupMessage message = new GroupMessage(
                groupId, sender, dto.getType(), dto.getContent(), mentions,
                async ? GroupMessageStatus.SENDING.getCode() : GroupMessageStatus.READY.getCode(),
                now, now, null);
        groupMessageRepository.save(message);
        Long id = message.getId();
        if (async) {
            executorService.execute(() -> {
                long st = 0;
                if (logger.isDebugEnabled()) {
                    logger.debug("Start saving message items asynchronously, message: {}, expected size: {}", id, memberSize);
                    st = System.currentTimeMillis();
                }
                List<GroupMessageItem> items = new ArrayList<>(batchSize);
                List<Long> userIds = groupMemberRepository.findUserIdByGroupIdAndStatus(groupId, MemberStatus.JOINED.getCode());
                for (Long userId : userIds) {
                    if (userId.equals(sender)) {
                        continue;
                    }
                    items.add(new GroupMessageItem(id, userId, now, null, null));
                    if (items.size() == batchSize) {
                        saveInBatch(items, batchSize);
                        items.clear();
                    }
                }
                if (!items.isEmpty()) {
                    saveInBatch(items, batchSize);
                    items.clear();
                }
                groupMessageRepository.updateStatus(id, GroupMessageStatus.READY.getCode());
                if (logger.isDebugEnabled()) {
                    logger.debug("Saved message items asynchronously in {} ms, message: {}, actual size: {}", System.currentTimeMillis() - st, id, userIds.size());
                }
            });
        } else {
            long st = 0;
            if (logger.isDebugEnabled()) {
                logger.debug("Start saving message items synchronously, message: {}, expected size: {}", id, memberSize);
                st = System.currentTimeMillis();
            }
            List<GroupMessageItem> items = groupMemberRepository.findUserIdByGroupIdAndStatus(groupId, MemberStatus.JOINED.getCode())
                    .stream()
                    .filter(userId -> !userId.equals(sender))
                    .map(userId -> new GroupMessageItem(id, userId, now, null, null))
                    .toList();
            saveInBatch(items, items.size());
            if (logger.isDebugEnabled()) {
                logger.debug("Saved message items synchronously in {} ms, message: {}, actual size: {}", System.currentTimeMillis() - st, id, items.size());
            }
        }
        return new GroupMessagePrimeVO(message.getId(), message.getSentTime(), message.getStatus());
    }

    void saveInBatch(List<GroupMessageItem> entities, int batchSize) {
        jdbcTemplate.batchUpdate(
                "insert into chat_group_message_item(message_id, recipient, event_time) values (?, ?, ?)",
                entities, batchSize, (ps, o) -> {
                    ps.setLong(1, o.getMessageId());
                    ps.setLong(2, o.getRecipient());
                    ps.setTimestamp(3, java.sql.Timestamp.from(o.getEventTime()));
                });
    }
}
