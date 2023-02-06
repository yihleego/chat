package io.leego.mock.controller;

import io.leego.mock.annotation.Privileged;
import io.leego.mock.dto.SignInDTO;
import io.leego.mock.dto.SignUpDTO;
import io.leego.mock.entity.User;
import io.leego.mock.service.UserService;
import io.leego.mock.vo.AccessTokenVO;
import io.leego.security.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Leego Yih
 */
@RestController
@RequestMapping
public class AccountController {
    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    /** Creates an account with the given username and password. */
    @Privileged
    @PostMapping("accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public AccessTokenVO signUp(@Validated @RequestBody SignUpDTO dto) {
        return userService.signUp(dto);
    }

    /** Creates a session with the given username and password. */
    @Privileged
    @PostMapping("sessions")
    @ResponseStatus(HttpStatus.CREATED)
    public AccessTokenVO signIn(@Validated @RequestBody SignInDTO dto) {
        return userService.signIn(dto);
    }

    /** Removes the session with the given access token. */
    @DeleteMapping("sessions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void signOut() {
        userService.signOut();
    }

    /** Returns the session with the given access token. */
    @GetMapping("sessions")
    @ResponseStatus(HttpStatus.OK)
    public Authentication getSession() {
        return userService.getSession();
    }

    /** Returns the user with the given username. */
    @GetMapping("users/{username}")
    @ResponseStatus(HttpStatus.OK)
    public User getUser(@PathVariable String username) {
        return userService.getUser(username);
    }

}
