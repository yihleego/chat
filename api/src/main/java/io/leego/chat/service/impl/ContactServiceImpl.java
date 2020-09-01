package io.leego.chat.service.impl;

import io.leego.chat.MockedSessions;
import io.leego.chat.Result;
import io.leego.chat.pojo.vo.ContactVO;
import io.leego.chat.service.ContactService;
import io.leego.chat.util.UserUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Yihleego
 */
@Service
public class ContactServiceImpl implements ContactService {

    @Override
    public Result<List<ContactVO>> listContact() {
        final Long userId = UserUtils.getUserId();
        return Result.buildSuccess(MockedSessions.getUsers().stream()
                .filter(user -> !Objects.equals(userId, user.getId()))
                .map(user -> new ContactVO(user.getId(), user.getNickname()))
                .collect(Collectors.toList()));
    }

}
