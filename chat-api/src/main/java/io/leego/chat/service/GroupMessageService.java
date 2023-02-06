package io.leego.chat.service;

import io.leego.chat.dto.GroupMessageCreateDTO;
import io.leego.chat.vo.GroupMessagePrimeVO;
import io.leego.chat.vo.GroupMessageStateVO;
import io.leego.chat.vo.GroupMessageVO;

import java.time.Instant;
import java.util.List;

/**
 * @author Leego Yih
 */
public interface GroupMessageService {

    GroupMessagePrimeVO createGroupMessage(GroupMessageCreateDTO dto);

    void takeGroupMessages(Long[] ids);

    void readGroupMessages(Long[] ids);

    void revokeGroupMessage(Long id);

    GroupMessageVO getGroupMessage(Long id);

    GroupMessageStateVO getGroupMessageState(Long id, Instant lastTime);

    List<GroupMessageVO> listGroupMessages(Long id, Short type);

}
