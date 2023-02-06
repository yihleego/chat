package io.leego.chat;

import io.leego.chat.constant.ContactStatus;
import io.leego.chat.dto.ContactRequestCreateDTO;
import io.leego.chat.entity.Contact;
import io.leego.chat.entity.ContactRequest;
import io.leego.chat.vo.ContactVO;
import io.leego.security.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

/**
 * @author Leego Yih
 */
@Rollback
@SpringBootTest
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ContactSenderTests extends ChatTests {
    private static final Logger logger = LoggerFactory.getLogger(ContactSenderTests.class);

    @BeforeEach
    public void before() {
        initAsSender();
    }

    @AfterEach
    public void after() {
        SecurityUtils.remove();
    }

    @Test
    public void testCreateContactRequest() {
        contactRepository.deleteAll();
        String uuid = UUID.randomUUID().toString();
        ContactRequestCreateDTO dto = new ContactRequestCreateDTO(RECIPIENT, uuid);
        contactService.createContactRequest(dto);
        // Assert
        List<ContactRequest> list = contactRequestRepository.findByRecipient(RECIPIENT, Pageable.ofSize(10));
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals(list.get(list.size() - 1).getMessage(), uuid);
        logger.debug("Create: {}", list.get(list.size() - 1));
    }

    @Test
    public void testListContacts() {
        Contact mock1 = mockContact(ContactStatus.ADDED.getCode());
        List<ContactVO> list1 = contactService.listContacts(null);
        logger.debug("List: {}", list1);
        // Assert
        boolean flag1 = list1.stream().anyMatch(o -> o.getId().equals(mock1.getId()));
        Assertions.assertTrue(flag1);
        contactRepository.delete(mock1);

        Contact mock2 = mockContact(ContactStatus.REMOVED.getCode());
        List<ContactVO> list2 = contactService.listContacts(null);
        logger.debug("List: {}", list2);
        // Assert
        boolean flag2 = list2.stream().noneMatch(o -> o.getId().equals(mock2.getId()));
        Assertions.assertTrue(flag2);
        contactRepository.delete(mock2);

        Contact mock3 = mockContact(ContactStatus.BLOCKED.getCode());
        List<ContactVO> list3 = contactService.listContacts(null);
        logger.debug("List: {}", list3);
        // Assert
        boolean flag3 = list3.stream().noneMatch(o -> o.getId().equals(mock3.getId()));
        Assertions.assertTrue(flag3);
        contactRepository.delete(mock3);
    }
}
