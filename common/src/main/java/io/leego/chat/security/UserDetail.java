package io.leego.chat.security;

public class UserDetail {
    private Long id;
    private String username;
    private String nickname;
    private String token;

    public UserDetail() {
    }

    public UserDetail(Long id, String username, String nickname, String token) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
