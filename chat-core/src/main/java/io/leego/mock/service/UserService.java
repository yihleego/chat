package io.leego.mock.service;

import io.leego.mock.dto.SignInDTO;
import io.leego.mock.dto.SignUpDTO;
import io.leego.mock.entity.User;
import io.leego.mock.vo.AccessTokenVO;
import io.leego.security.Authentication;

import java.util.List;
import java.util.Map;

/**
 * @author Leego Yih
 */
public interface UserService {

    AccessTokenVO signUp(SignUpDTO dto);

    AccessTokenVO signIn(SignInDTO dto);

    void signOut();

    Authentication getSession();

    User getUser(String username);

    User getUser(Long id);

    Map<Long, User> getUserMap(List<Long> ids);

}
