package io.leego.chat.service.impl;

import io.leego.chat.MockedSessions;
import io.leego.chat.Result;
import io.leego.chat.UserDetail;
import io.leego.chat.pojo.dto.SignInDTO;
import io.leego.chat.service.AccountService;
import io.leego.chat.util.UserUtils;
import org.springframework.stereotype.Service;

/**
 * @author Yihleego
 */
@Service
public class AccountServiceImpl implements AccountService {

    @Override
    public Result<UserDetail> signIn(SignInDTO signInDTO) {
        UserDetail user = MockedSessions.getByUsername(signInDTO.getUsername());
        if (user == null) {
            return Result.buildFailure("Incorrect username and password");
        }
        UserUtils.setUser(user);
        return Result.buildSuccess(user);
    }

    @Override
    public Result<Void> signOut() {
        UserUtils.removeUser();
        return Result.buildSuccess();
    }

    @Override
    public Result<UserDetail> getUser() {
        UserDetail user = UserUtils.getUser();
        if (user == null) {
            return Result.buildFailure();
        }
        return Result.buildSuccess(user);
    }

}
