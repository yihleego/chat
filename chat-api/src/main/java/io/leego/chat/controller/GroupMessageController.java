package io.leego.chat.controller;

import io.leego.chat.dto.GroupMessageCreateDTO;
import io.leego.chat.exception.ForbiddenException;
import io.leego.chat.exception.NotFoundException;
import io.leego.chat.service.GroupMessageService;
import io.leego.chat.vo.GroupMessagePrimeVO;
import io.leego.chat.vo.GroupMessageStateVO;
import io.leego.chat.vo.GroupMessageVO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping(value = "messages", params = "target=group")
public class GroupMessageController {
    private final GroupMessageService groupMessageService;

    public GroupMessageController(GroupMessageService groupMessageService) {
        this.groupMessageService = groupMessageService;
    }

    /**
     * Creates a message.
     * Notes that the message may be sent asynchronously.
     * Please check the status of the message before sending it.
     *
     * @param message must not be {@literal null}.
     * @return the saved message.
     * @throws ForbiddenException if the current user is not a member of the group.
     * @see io.leego.chat.constant.MessageType
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupMessagePrimeVO createGroupMessage(@Validated @RequestBody GroupMessageCreateDTO message) {
        return groupMessageService.createGroupMessage(message);
    }

    /**
     * Takes the messages with the given IDs.
     *
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     */
    @PatchMapping(value = "{ids}", params = "action=take")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void takeGroupMessages(@PathVariable Long[] ids) {
        groupMessageService.takeGroupMessages(ids);
    }

    /**
     * Reads the messages with the given IDs.
     *
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     */
    @PatchMapping(value = "{ids}", params = "action=read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void readGroupMessages(@PathVariable Long[] ids) {
        groupMessageService.readGroupMessages(ids);
    }

    /**
     * Revokes the message with the given ID.
     *
     * @param id must not be {@literal null}.
     * @throws ForbiddenException if the message is not allowed to be revoked.
     * @throws NotFoundException  if the message is not found, or does not belong to the user.
     */
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeGroupMessage(@PathVariable Long id) {
        groupMessageService.revokeGroupMessage(id);
    }

    /**
     * Returns the message with the given ID.
     * Notes that the message may be sent asynchronously.
     * Please check the status of the message before sending it.
     *
     * @param id must not be {@literal null}.
     * @return the message with the given ID.
     * @throws NotFoundException if the message is not found, or does not belong to the user.
     */
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public GroupMessageVO getGroupMessage(@PathVariable Long id) {
        return groupMessageService.getGroupMessage(id);
    }

    /**
     * Returns the message state with the given ID.
     *
     * @param id       must not be {@literal null}.
     * @param lastTime could be {@literal null}.
     * @return the message state with the given ID.
     * @throws NotFoundException if the message is not found, or does not belong to the user.
     */
    @GetMapping("{id}/states")
    @ResponseStatus(HttpStatus.OK)
    public GroupMessageStateVO getGroupMessageState(@PathVariable Long id, @RequestParam(required = false) Instant lastTime) {
        return groupMessageService.getGroupMessageState(id, lastTime);
    }

    /**
     * Returns the messages on the client of the current user.
     * The messages may contain some that have already been fetched.
     *
     * <ul>
     * <li>{@code SENDER(0)} - Returns the message sent by the current user.
     * <li>{@code RECIPIENT(1)} - Returns the message received by the current user.
     * </ul>
     *
     * @param id   could be {@literal null}, cursor.
     * @param type must not be {@literal null}.
     * @return the remaining messages.
     * @throws ForbiddenException if the client of the current user is not capable.
     * @throws NotFoundException  if the given ID is not {@literal null}, but the message is not found or does not belong to the user.
     * @see io.leego.chat.constant.UserType
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<GroupMessageVO> listGroupMessages(@RequestParam(required = false) Long id, @RequestParam Short type) {
        return groupMessageService.listGroupMessages(id, type);
    }

}
