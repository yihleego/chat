package io.leego.chat.service;

import io.leego.chat.dto.MessageCreateDTO;
import io.leego.chat.vo.MessagePrimeVO;
import io.leego.chat.vo.MessageVO;

import java.util.List;

/**
 * @author Leego Yih
 */
public interface MessageService {

    MessagePrimeVO createMessage(MessageCreateDTO dto);

    void takeMessages(Long[] ids);

    void readMessages(Long[] ids);

    void revokeMessage(Long id);

    MessageVO getMessage(Long id);

    List<MessageVO> listMessages(Long id, Short type);

}
