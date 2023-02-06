package io.leego.mock.service.impl;

import io.leego.mock.constant.Avatars;
import io.leego.mock.dto.DeviceDTO;
import io.leego.mock.dto.SignInDTO;
import io.leego.mock.dto.SignUpDTO;
import io.leego.mock.entity.User;
import io.leego.mock.repository.UserRepository;
import io.leego.mock.service.UserService;
import io.leego.mock.vo.AccessTokenVO;
import io.leego.security.Authentication;
import io.leego.security.SecurityKeys;
import io.leego.security.SecurityManager;
import io.leego.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Leego Yih
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final SecurityManager securityManager;

    public UserServiceImpl(UserRepository userRepository, SecurityManager securityManager) {
        this.userRepository = userRepository;
        this.securityManager = securityManager;
    }

    @Override
    @Transactional
    public AccessTokenVO signUp(SignUpDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new HttpClientErrorException(HttpStatus.CONFLICT);
        }
        User user = new User(null,
                dto.getUsername(), encryptPassword(dto.getPassword()),
                dto.getNickname(), Avatars.random());
        userRepository.save(user);
        return setSession(user, dto);
    }

    @Override
    @Transactional
    public AccessTokenVO signIn(SignInDTO dto) {
        User user = userRepository.findByUsername(dto.getUsername());
        if (user == null || !user.getPassword().equals(encryptPassword(dto.getPassword()))) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
        return setSession(user, dto);
    }

    @Override
    public void signOut() {
        securityManager.remove(SecurityUtils.getToken());
    }

    @Override
    public Authentication getSession() {
        Authentication a = SecurityUtils.get();
        if (a == null) {
            return null;
        }
        securityManager.refresh(a.token(), Duration.ofDays(7));
        return a;
    }

    @Override
    public User getUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public User getUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public Map<Long, User> getUserMap(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        return ((List<User>) userRepository.findAllById(ids)).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    private AccessTokenVO setSession(User user, DeviceDTO dto) {
        String token = SecurityUtils.randomToken();
        Authentication a = new Authentication(
                user.getId(), user.getUsername(), user.getNickname(), user.getAvatar(),
                dto.getDeviceId(), dto.getDeviceType(), dto.getClientType(), token);
        Duration timeout = Duration.ofDays(7);
        securityManager.set(token, a, timeout);
        return new AccessTokenVO(token, SecurityKeys.TOKEN_TYPE, timeout.toSeconds());
    }

    private String encryptPassword(String s) {
        // TODO
        return s;
    }
}
