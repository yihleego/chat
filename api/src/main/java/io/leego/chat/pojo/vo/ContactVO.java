package io.leego.chat.pojo.vo;

/**
 * @author Yihleego
 */
public class ContactVO {
    private Long id;
    private String nickname;

    public ContactVO() {
    }

    public ContactVO(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
