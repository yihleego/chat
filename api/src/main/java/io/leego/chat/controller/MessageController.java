package io.leego.chat.controller;

import io.leego.chat.Result;
import io.leego.chat.pojo.dto.MessageSaveDTO;
import io.leego.chat.pojo.entity.Message;
import io.leego.chat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Yihleego
 */
@RestController
@RequestMapping("messages")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @GetMapping("{id}")
    public Result<Message> getMessage(@PathVariable String id) {
        return messageService.getMessage(id);
    }

    @PostMapping
    public Result<Message> saveMessage(@Validated @RequestBody MessageSaveDTO save) {
        return messageService.saveMessage(save);
    }

    @PatchMapping("{id}")
    public Result<Void> markMessage(@PathVariable String id) {
        return messageService.markMessage(id);
    }

    @GetMapping("unread")
    public Result<List<Message>> listUnreadMessage(@RequestParam(required = false) String anchor) {
        return messageService.listUnreadMessage(anchor);
    }

}
