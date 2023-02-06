package io.leego.chat.controller;

import io.leego.chat.core.Message;
import io.leego.chat.core.Messages;
import io.leego.chat.service.MessageService;
import io.leego.chat.vo.MessageVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collector;

/**
 * @author Leego Yih
 */
@RestController
@RequestMapping(value = "messages", consumes = MediaType.APPLICATION_PROTOBUF_VALUE, produces = MediaType.APPLICATION_PROTOBUF_VALUE)
public class MessageProtobufController {
    private final MessageService messageService;

    public MessageProtobufController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Message getMessage(@PathVariable Long id) {
        return toFull(messageService.getMessage(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Messages listMessages(@RequestParam(required = false) Long id, @RequestParam Short type) {
        return toFulls(messageService.listMessages(id, type));
    }

    private Message toFull(MessageVO o) {
        return toFullBuilder(o).build();
    }

    private Messages toFulls(List<MessageVO> list) {
        if (list.isEmpty()) {
            return Messages.getDefaultInstance();
        }
        return list.stream()
                .map(this::toFullBuilder)
                .collect(Collector.of(
                        Messages::newBuilder,
                        Messages.Builder::addMessage,
                        (left, right) -> left.addAllMessage(right.getMessageList())))
                .build();
    }

    private Message.Builder toFullBuilder(MessageVO o) {
        Message.Builder msg = Message.newBuilder()
                .setId(o.getId())
                .setSender(o.getSender())
                .setRecipient(o.getRecipient())
                .setTaken(o.isTaken())
                .setSeen(o.isSeen())
                .setRevoked(o.isRevoked())
                .setSentTime(o.getSentTime().toEpochMilli());
        if (!o.isRevoked()) {
            if (o.getType() != null) {
                msg.setType(o.getType());
            }
            if (o.getContent() != null) {
                msg.setContent(o.getContent());
            }
        }
        return msg;
    }
}
