package io.leego.chat.controller;

import io.leego.chat.dto.MessageCreateDTO;
import io.leego.chat.exception.ForbiddenException;
import io.leego.chat.exception.NotFoundException;
import io.leego.chat.service.MessageService;
import io.leego.chat.vo.MessagePrimeVO;
import io.leego.chat.vo.MessageVO;
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

import java.util.List;

/**
 * @author Leego Yih
 */
@RestController
@RequestMapping("messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Creates a message.
     *
     * @param message must not be {@literal null}.
     * @return the saved message.
     * @throws ForbiddenException if there is no contact between the sender and the recipient.
     * @see io.leego.chat.constant.MessageType
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessagePrimeVO createMessage(@Validated @RequestBody MessageCreateDTO message) {
        return messageService.createMessage(message);
    }

    /**
     * Takes the messages with the given IDs.
     *
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     */
    @PatchMapping(value = "{ids}", params = "action=take")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void takeMessages(@PathVariable Long[] ids) {
        messageService.takeMessages(ids);
    }

    /**
     * Reads the messages with the given IDs.
     *
     * @param ids must not be {@literal null} nor contain any {@literal null} values.
     */
    @PatchMapping(value = "{ids}", params = "action=read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void readMessages(@PathVariable Long[] ids) {
        messageService.readMessages(ids);
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
    public void revokeMessage(@PathVariable Long id) {
        messageService.revokeMessage(id);
    }

    /**
     * Returns the message with the given ID.
     *
     * @param id must not be {@literal null}.
     * @return the message with the given ID.
     * @throws NotFoundException if the message is not found, or does not belong to the user.
     */
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public MessageVO getMessage(@PathVariable Long id) {
        return messageService.getMessage(id);
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
    public List<MessageVO> listMessages(@RequestParam(required = false) Long id, @RequestParam Short type) {
        return messageService.listMessages(id, type);
    }

}
