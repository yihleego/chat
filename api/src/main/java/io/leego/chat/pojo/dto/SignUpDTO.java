package io.leego.chat.pojo.dto;

import javax.validation.constraints.NotNull;

public class SignUpDTO {
    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String nickname;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
