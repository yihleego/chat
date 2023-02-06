package io.leego.chat.service;

import io.leego.chat.dto.GroupCreateDTO;
import io.leego.chat.dto.GroupMemberCreateDTO;
import io.leego.chat.vo.GroupMemberVO;
import io.leego.chat.vo.GroupVO;

import java.time.Instant;
import java.util.List;

/**
 * @author Leego Yih
 */
public interface GroupService {

    GroupVO createGroup(GroupCreateDTO dto);

    void deleteGroup(Long groupId);

    GroupVO getGroup(Long groupId);

    List<GroupVO> listGroups(Instant lastTime);

    void createMember(GroupMemberCreateDTO dto);

    void leaveGroup(Long groupId);

    void removeMember(Long groupId, Long userId);

    GroupMemberVO getMember(Long groupId, Long userId);

    List<GroupMemberVO> listMembers(Long groupId, Instant lastTime);

}
