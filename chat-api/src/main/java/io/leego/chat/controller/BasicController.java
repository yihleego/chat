package io.leego.chat.controller;

import io.leego.chat.core.Instance;
import io.leego.chat.exception.NotFoundException;
import io.leego.chat.manager.ChatManager;
import io.leego.chat.vo.InstanceVO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Leego Yih
 */
@RestController
@RequestMapping
public class BasicController {
    private final ChatManager chatManager;

    public BasicController(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    /**
     * Returns an available instance.
     *
     * @throws NotFoundException if no instance exists.
     */
    @GetMapping("instances")
    @ResponseStatus(HttpStatus.OK)
    public InstanceVO getInstance() {
        Instance o = chatManager.nextInstance();
        if (o == null) {
            throw new NotFoundException();
        }
        return new InstanceVO(o.getHost(), o.getRawPort(), o.getWsPort());
    }

}
