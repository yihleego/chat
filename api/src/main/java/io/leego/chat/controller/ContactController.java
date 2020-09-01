package io.leego.chat.controller;

import io.leego.chat.Result;
import io.leego.chat.pojo.vo.ContactVO;
import io.leego.chat.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Yihleego
 */
@RestController
@RequestMapping("contacts")
public class ContactController {
    @Autowired
    private ContactService contactService;

    @GetMapping
    public Result<List<ContactVO>> listContact() {
        return contactService.listContact();
    }

}
