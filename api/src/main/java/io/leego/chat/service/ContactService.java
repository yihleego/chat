package io.leego.chat.service;

import io.leego.chat.Result;
import io.leego.chat.pojo.vo.ContactVO;

import java.util.List;

/**
 * @author Yihleego
 */
public interface ContactService {

    Result<List<ContactVO>> listContact();

}
