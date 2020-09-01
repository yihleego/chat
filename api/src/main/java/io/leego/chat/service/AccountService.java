package io.leego.chat.service;

import io.leego.chat.Result;
import io.leego.chat.UserDetail;
import io.leego.chat.pojo.dto.SignInDTO;

/**
 * @author Yihleego
 */
public interface AccountService {

    Result<UserDetail> signIn(SignInDTO signInDTO);

    Result<Void> signOut();

    Result<UserDetail> getUser();

}
