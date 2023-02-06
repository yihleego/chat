package io.leego.chat.controller;

import io.leego.chat.dto.GroupCreateDTO;
import io.leego.chat.dto.GroupMemberCreateDTO;
import io.leego.chat.exception.ForbiddenException;
import io.leego.chat.exception.NotAcceptableException;
import io.leego.chat.exception.NotFoundException;
import io.leego.chat.service.GroupService;
import io.leego.chat.vo.GroupMemberVO;
import io.leego.chat.vo.GroupVO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

/**
 * @author Leego Yih
 */
@RestController
@RequestMapping("groups")
public class GroupController {
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * Creates a group.
     *
     * @param group must not be {@literal null}.
     * @throws NotAcceptableException if too many groups.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupVO createGroup(@Validated @RequestBody GroupCreateDTO group) {
        return groupService.createGroup(group);
    }

    /**
     * Deletes the group with the given ID.
     *
     * @param groupId must not be {@literal null}.
     * @throws NotFoundException  if the group is not found.
     * @throws ForbiddenException if the current user is not the owner of the group.
     */
    @DeleteMapping("{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(@PathVariable Long groupId) {
        groupService.deleteGroup(groupId);
    }

    /**
     * Returns the group with the given ID.
     *
     * @param groupId must not be {@literal null}.
     * @return the group with the given ID.
     * @throws NotFoundException  if the group is not found.
     * @throws ForbiddenException if the current user is not a member of the group.
     */
    @GetMapping("{groupId}")
    @ResponseStatus(HttpStatus.OK)
    public GroupVO getGroup(@PathVariable Long groupId) {
        return groupService.getGroup(groupId);
    }

    /**
     * Returns all groups that the current user has joined.
     *
     * @param lastTime could be {@literal null}.
     * @return all groups that the current user has joined.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<GroupVO> listGroups(@RequestParam(required = false) Instant lastTime) {
        return groupService.listGroups(lastTime);
    }

    /**
     * Invites the user to join the group.
     *
     * @param groupId must not be {@literal null}.
     * @param member  must not be {@literal null}.
     * @throws NotFoundException      if the group is not found.
     * @throws ForbiddenException     if the current user is not a member of the group.
     * @throws NotAcceptableException if too many members.
     */
    @PostMapping("{groupId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public void createMember(@PathVariable Long groupId, @Validated @RequestBody GroupMemberCreateDTO member) {
        member.setGroupId(groupId);
        groupService.createMember(member);
    }

    /**
     * Leaves the group.
     *
     * @param groupId must not be {@literal null}.
     * @throws NotFoundException  if the group is not found.
     * @throws ForbiddenException if the current user is the owner of the group.
     */
    @DeleteMapping("{groupId}/members")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveGroup(@PathVariable Long groupId) {
        groupService.leaveGroup(groupId);
    }

    /**
     * Removes the member from the group.
     *
     * @param groupId must not be {@literal null}.
     * @param userId  must not be {@literal null}.
     * @throws NotFoundException  if the group is not found.
     * @throws ForbiddenException if the current user is not the owner of the group.
     */
    @DeleteMapping("{groupId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(@PathVariable Long groupId, @PathVariable Long userId) {
        groupService.removeMember(groupId, userId);
    }

    /**
     * Returns the member of the group.
     *
     * @param groupId must not be {@literal null}.
     * @param userId  must not be {@literal null}.
     * @return the member of the group.
     * @throws NotFoundException  if the group is not found.
     * @throws ForbiddenException if the current user is not a member of the group.
     */
    @GetMapping("{groupId}/members/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public GroupMemberVO getMember(@PathVariable Long groupId, @PathVariable Long userId) {
        return groupService.getMember(groupId, userId);
    }

    /**
     * Returns all members of the group.
     *
     * @param groupId  must not be {@literal null}.
     * @param lastTime could be {@literal null}.
     * @return all members of the group.
     * @throws ForbiddenException if the current user is not a member of the group.
     */
    @GetMapping("{groupId}/members")
    @ResponseStatus(HttpStatus.OK)
    public List<GroupMemberVO> listMembers(@PathVariable Long groupId, @RequestParam(required = false) Instant lastTime) {
        return groupService.listMembers(groupId, lastTime);
    }

}
