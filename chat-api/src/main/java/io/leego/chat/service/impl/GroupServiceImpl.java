package io.leego.chat.service.impl;

import io.leego.chat.config.ChatProperties;
import io.leego.chat.constant.GroupStatus;
import io.leego.chat.constant.MemberStatus;
import io.leego.chat.dto.GroupCreateDTO;
import io.leego.chat.dto.GroupMemberCreateDTO;
import io.leego.chat.entity.Group;
import io.leego.chat.entity.GroupMember;
import io.leego.chat.exception.ForbiddenException;
import io.leego.chat.exception.NotAcceptableException;
import io.leego.chat.exception.NotFoundException;
import io.leego.chat.manager.GroupManager;
import io.leego.chat.repository.GroupMemberRepository;
import io.leego.chat.repository.GroupRepository;
import io.leego.chat.service.GroupService;
import io.leego.chat.vo.GroupMemberVO;
import io.leego.chat.vo.GroupVO;
import io.leego.mock.constant.Avatars;
import io.leego.mock.entity.User;
import io.leego.mock.service.UserService;
import io.leego.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Leego Yih
 */
@Service
public class GroupServiceImpl implements GroupService {
    private static final Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupManager groupManager;
    private final ChatProperties properties;
    private final UserService userService;

    public GroupServiceImpl(
            GroupRepository groupRepository,
            GroupMemberRepository groupMemberRepository,
            GroupManager groupManager,
            ChatProperties properties,
            UserService userService) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupManager = groupManager;
        this.properties = properties;
        this.userService = userService;
    }

    @Override
    @Transactional
    public GroupVO createGroup(GroupCreateDTO dto) {
        Long userId = getUserId();
        int count = groupRepository.countByOwnerAndStatus(userId, GroupStatus.ACTIVE.getCode());
        if (count >= properties.getGroup().getMaxSize()) {
            throw new NotAcceptableException("Too many groups");
        }
        Group group = new Group(dto.getName(), Avatars.random(), userId, properties.getMember().getMaxSize(), GroupStatus.ACTIVE.getCode());
        groupRepository.save(group);
        GroupMember member = new GroupMember(group.getId(), group.getOwner(), null, MemberStatus.JOINED.getCode());
        groupMemberRepository.save(member);
        groupManager.addMember(member.getGroupId(), member.getUserId());
        return toGroupVO(group);
    }

    @Override
    @Transactional
    public void deleteGroup(Long groupId) {
        Long userId = getUserId();
        Group group = groupRepository.findByIdAndStatus(groupId, GroupStatus.ACTIVE.getCode());
        if (group == null) {
            throw noGroup(groupId);
        }
        if (!userId.equals(group.getOwner())) {
            throw notOwner(groupId, userId);
        }
        short status = GroupStatus.DELETED.getCode();
        int updated = groupRepository.updateStatus(group.getId(), group.getStatus(), status, Instant.now());
        if (updated <= 0) {
            logger.error("Update group [{}] status [{}] -> [{}] failed", group.getId(), group.getStatus(), status);
            return;
        }
        groupManager.removeAllMembers(groupId);
    }

    @Override
    public GroupVO getGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .map(this::toGroupVO)
                .orElseThrow(() -> noGroup(groupId));
    }

    @Override
    public List<GroupVO> listGroups(Instant lastTime) {
        Long userId = getUserId();
        Pageable pageable = Pageable.ofSize(properties.getMember().getFetchSize());
        List<GroupMember> members = lastTime == null
                ? groupMemberRepository.findByUserId(userId, pageable)
                : groupMemberRepository.findByUserIdAndUpdatedTimeAfter(userId, lastTime, pageable);
        if (members.isEmpty()) {
            return Collections.emptyList();
        }
        List<Group> groups = groupRepository.findAllById(members.stream().map(GroupMember::getGroupId).toList());
        if (groups.isEmpty()) {
            return Collections.emptyList();
        }
        return groups.stream().map(this::toGroupVO).toList();
    }

    @Override
    @Transactional
    public void createMember(GroupMemberCreateDTO dto) {
        Long groupId = dto.getGroupId();
        Long userId = dto.getUserId();
        checkMember(groupId);
        Group group = groupRepository.findByIdAndStatus(groupId, GroupStatus.ACTIVE.getCode());
        if (group == null) {
            throw noGroup(groupId);
        }
        if (groupManager.countMembers(groupId) >= group.getSize()) {
            throw new NotAcceptableException("Too many members");
        }
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (member != null) {
            if (member.getStatus() == MemberStatus.JOINED.getCode()) {
                logger.warn("Member already exists {} -> {}", groupId, userId);
                return;
            }
            logger.debug("Delete original member: {}", member);
            groupMemberRepository.deleteById(member.getId());
        }
        groupMemberRepository.save(new GroupMember(groupId, userId, null, MemberStatus.JOINED.getCode()));
        groupManager.addMember(groupId, userId);
    }

    @Override
    @Transactional
    public void leaveGroup(Long groupId) {
        Group group = groupRepository.findByIdAndStatus(groupId, GroupStatus.ACTIVE.getCode());
        if (group == null) {
            throw noGroup(groupId);
        }
        Long userId = getUserId();
        if (userId.equals(group.getOwner())) {
            throw new ForbiddenException("Owner cannot leave group [%d]".formatted(groupId));
        }
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (member == null) {
            throw notMember(groupId, userId);
        }
        if (member.getStatus() != MemberStatus.JOINED.getCode()) {
            logger.error("Cannot leave group [{}] with status {}", member.getId(), member.getStatus());
            return;
        }
        short status = MemberStatus.LEFT.getCode();
        int updated = groupMemberRepository.updateStatus(member.getId(), member.getStatus(), status, Instant.now());
        if (updated <= 0) {
            logger.error("Update member [{}] status [{}] -> [{}] failed", member.getId(), member.getStatus(), status);
            return;
        }
        groupManager.removeMember(groupId, userId);
    }

    @Override
    @Transactional
    public void removeMember(Long groupId, Long userId) {
        Group group = groupRepository.findByIdAndStatus(groupId, GroupStatus.ACTIVE.getCode());
        if (group == null) {
            throw noGroup(groupId);
        }
        Long owner = getUserId();
        if (!owner.equals(group.getOwner())) {
            throw notOwner(groupId, userId);
        }
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (member == null) {
            throw notMember(groupId, userId);
        }
        if (member.getStatus() != MemberStatus.JOINED.getCode()) {
            logger.error("Cannot remove member [{}] with status {}", member.getId(), member.getStatus());
            return;
        }
        short status = MemberStatus.REMOVED.getCode();
        int updated = groupMemberRepository.updateStatus(member.getId(), member.getStatus(), status, Instant.now());
        if (updated <= 0) {
            logger.error("Update member [{}] status [{}] -> [{}] failed", member.getId(), member.getStatus(), status);
            return;
        }
        groupManager.removeMember(groupId, userId);
    }

    @Override
    public GroupMemberVO getMember(Long groupId, Long userId) {
        checkMember(groupId);
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        if (member == null) {
            throw notMember(groupId, userId);
        }
        return toMemberVO(member);
    }

    @Override
    public List<GroupMemberVO> listMembers(Long groupId, Instant lastTime) {
        checkMember(groupId);
        Pageable pageable = Pageable.ofSize(properties.getMember().getFetchSize());
        List<GroupMember> members = lastTime == null
                ? groupMemberRepository.findByGroupId(groupId, pageable)
                : groupMemberRepository.findByGroupIdAndUpdatedTimeAfter(groupId, lastTime, pageable);
        if (members.isEmpty()) {
            return Collections.emptyList();
        }
        return toMemberVOs(members);
    }

    RuntimeException noGroup(Long groupId) {
        return new NotFoundException("Cannot find group [%d]".formatted(groupId));
    }

    RuntimeException notMember(Long groupId, Long userId) {
        return new ForbiddenException("User [%d] is not a member of the group [%d]".formatted(userId, groupId));
    }

    RuntimeException notOwner(Long groupId, Long userId) {
        return new ForbiddenException("User [%d] is not the owner of the group [%d]".formatted(userId, groupId));
    }

    void checkMember(Long groupId) {
        Long userId = getUserId();
        if (!groupManager.isMember(groupId, userId)) {
            throw new ForbiddenException("User [%d] is not a member of the group [%d]".formatted(userId, groupId));
        }
    }

    GroupVO toGroupVO(Group o) {
        return new GroupVO(o.getId(), o.getName(), o.getAvatar(), o.getOwner(), o.getCreatedTime(), o.getUpdatedTime());
    }

    GroupMemberVO toMemberVO(GroupMember o) {
        User user = userService.getUser(o.getUserId());
        return new GroupMemberVO(
                o.getUserId(),
                user == null ? null : user.getNickname(),
                o.getAlias(),
                o.getCreatedTime(),
                o.getUpdatedTime());
    }

    List<GroupMemberVO> toMemberVOs(List<GroupMember> list) {
        Map<Long, User> users = userService.getUserMap(list.stream().map(GroupMember::getUserId).toList());
        return list.stream()
                .map(o -> {
                    User user = users.get(o.getUserId());
                    return new GroupMemberVO(
                            o.getUserId(),
                            user == null ? null : user.getNickname(),
                            o.getAlias(),
                            o.getCreatedTime(),
                            o.getUpdatedTime());
                })
                .toList();
    }

    Long getUserId() {
        return SecurityUtils.getUserId();
    }
}
