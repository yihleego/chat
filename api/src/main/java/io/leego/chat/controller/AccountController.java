package io.leego.chat.controller;

import io.leego.chat.Result;
import io.leego.chat.UserDetail;
import io.leego.chat.pojo.dto.SignInDTO;
import io.leego.chat.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Yihleego
 */
@RestController
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping("sessions")
    public Result<UserDetail> signIn(@Validated @RequestBody SignInDTO signInDTO) {
        return accountService.signIn(signInDTO);
    }

    @DeleteMapping("sessions")
    public Result<Void> signOut() {
        return accountService.signOut();
    }

    @GetMapping("sessions")
    public Result<UserDetail> getUser() {
        return accountService.getUser();
    }
}
