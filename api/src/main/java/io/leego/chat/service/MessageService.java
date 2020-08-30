package io.leego.chat.service;

import io.leego.chat.Result;
import io.leego.chat.pojo.dto.MessageSaveDTO;
import io.leego.chat.pojo.entity.Message;

import java.util.List;

/**
 * @author Yihleego
 */
public interface MessageService {

    Result<Message> getMessage(String id);

    Result<Message> saveMessage(MessageSaveDTO save);

    Result<Void> markMessage(String id);

    Result<List<Message>> listUnreadMessage(String anchor);

}
