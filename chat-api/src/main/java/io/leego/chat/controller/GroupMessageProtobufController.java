package io.leego.chat.controller;

import io.leego.chat.core.GroupMessage;
import io.leego.chat.core.GroupMessages;
import io.leego.chat.core.Mention;
import io.leego.chat.service.GroupMessageService;
import io.leego.chat.vo.GroupMessageVO;
import io.leego.chat.vo.MentionVO;
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
@RequestMapping(value = "messages", params = "target=group", consumes = MediaType.APPLICATION_PROTOBUF_VALUE, produces = MediaType.APPLICATION_PROTOBUF_VALUE)
public class GroupMessageProtobufController {
    private final GroupMessageService groupMessageService;

    public GroupMessageProtobufController(GroupMessageService groupMessageService) {
        this.groupMessageService = groupMessageService;
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public GroupMessage getGroupMessage(@PathVariable Long id) {
        return toFull(groupMessageService.getGroupMessage(id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public GroupMessages listGroupMessages(@RequestParam(required = false) Long id, @RequestParam Short type) {
        return toFulls(groupMessageService.listGroupMessages(id, type));
    }

    private GroupMessage toFull(GroupMessageVO o) {
        return toFullBuilder(o).build();
    }

    private GroupMessages toFulls(List<GroupMessageVO> list) {
        if (list.isEmpty()) {
            return GroupMessages.getDefaultInstance();
        }
        return list.stream()
                .map(this::toFullBuilder)
                .collect(Collector.of(
                        GroupMessages::newBuilder,
                        GroupMessages.Builder::addMessage,
                        (left, right) -> left.addAllMessage(right.getMessageList())))
                .build();
    }

    private GroupMessage.Builder toFullBuilder(GroupMessageVO o) {
        GroupMessage.Builder msg = GroupMessage.newBuilder()
                .setId(o.getId())
                .setGroupId(o.getGroupId())
                .setSender(o.getSender())
                .setTaken(o.isTaken())
                .setSeen(o.isSeen())
                .setRevoked(o.isRevoked())
                .setSentTime(o.getSentTime().toEpochMilli())
                .setStatus(o.getStatus());
        // Hide the content if it has been revoked
        if (!o.isRevoked()) {
            if (o.getType() != null) {
                msg.setType(o.getType());
            }
            if (o.getContent() != null) {
                msg.setContent(o.getContent());
            }
            if (o.getMentions() != null && o.getMentions().length > 0) {
                MentionVO[] mentions = o.getMentions();
                for (MentionVO mention : mentions) {
                    msg.addMention(Mention.newBuilder()
                            .setUserId(mention.getUserId())
                            .setIndex(mention.getIndex()));
                }
            }
        }
        return msg;
    }
}
