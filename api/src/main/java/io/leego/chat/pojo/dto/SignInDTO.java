package io.leego.chat.pojo.dto;

import javax.validation.constraints.NotNull;

public class SignInDTO {
    @NotNull
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
