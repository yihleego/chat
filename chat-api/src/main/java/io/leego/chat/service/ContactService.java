package io.leego.chat.service;

import io.leego.chat.dto.ContactRequestCreateDTO;
import io.leego.chat.vo.ContactRequestVO;
import io.leego.chat.vo.ContactVO;

import java.time.Instant;
import java.util.List;

/**
 * @author Leego Yih
 */
public interface ContactService {

    void blockContact(Long recipient);

    void removeContact(Long recipient);

    ContactVO getContact(Long recipient);

    List<ContactVO> listContacts(Instant lastTime);

    void createContactRequest(ContactRequestCreateDTO contactRequestCreateDTO);

    void acceptContactRequest(Long id);

    void rejectContactRequest(Long id);

    ContactRequestVO getContactRequest(Long id);

    List<ContactRequestVO> listContactRequests(Instant lastTime);

}
