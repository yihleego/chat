package io.leego.chat.service;

import io.leego.chat.Result;
import io.leego.chat.pojo.dto.SignInDTO;
import io.leego.chat.security.UserDetail;

/**
 * @author Yihleego
 */
public interface AccountService {

    Result<UserDetail> signIn(SignInDTO signInDTO);

    Result<Void> signOut();

    Result<UserDetail> getUser();

}
