package io.leego.chat;

import io.leego.chat.constant.RequestStatus;
import io.leego.chat.entity.Contact;
import io.leego.chat.entity.ContactRequest;
import io.leego.chat.vo.ContactRequestVO;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Leego Yih
 */
@Rollback
@SpringBootTest
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ContactRecipientTests extends ChatTests {
    private static final Logger logger = LoggerFactory.getLogger(ContactRecipientTests.class);

    @BeforeEach
    public void before() {
        initAsRecipient();
    }

    @AfterEach
    public void after() {
        SecurityUtils.remove();
    }

    @Test
    public void testUpdateContactRequestAccepted() {
        ContactRequest mock = mockContactRequest(RequestStatus.APPLIED.getCode());
        contactService.acceptContactRequest(mock.getId());
        // Assert
        ContactRequest o = contactRequestRepository.findById(mock.getId()).orElse(null);
        Assertions.assertNotNull(o);
        Assertions.assertEquals(o.getStatus(), RequestStatus.ACCEPTED.getCode());
        Contact contact1 = contactRepository.findBySenderAndRecipient(mock.getSender(), mock.getRecipient());
        Contact contact2 = contactRepository.findBySenderAndRecipient(mock.getRecipient(), mock.getSender());
        Assertions.assertNotNull(contact1);
        Assertions.assertNotNull(contact2);
        logger.debug("Accept: {}", o);
    }

    @Test
    public void testUpdateContactRequestRejected() {
        ContactRequest mock = mockContactRequest(RequestStatus.APPLIED.getCode());
        contactService.rejectContactRequest(mock.getId());
        // Assert
        ContactRequest o = contactRequestRepository.findById(mock.getId()).orElse(null);
        Assertions.assertNotNull(o);
        Assertions.assertEquals(o.getStatus(), RequestStatus.REJECTED.getCode());
        Contact contact1 = contactRepository.findBySenderAndRecipient(mock.getSender(), mock.getRecipient());
        Contact contact2 = contactRepository.findBySenderAndRecipient(mock.getRecipient(), mock.getSender());
        Assertions.assertNull(contact1);
        Assertions.assertNull(contact2);
        logger.debug("Reject: {}", o);
    }

    @Test
    @Transactional
    public void testListContactRequests() {
        ContactRequest mock1 = mockContactRequest(SENDER, RECIPIENT, RequestStatus.APPLIED.getCode());
        ContactRequest mock2 = mockContactRequest(ANYONE, RECIPIENT, RequestStatus.APPLIED.getCode());
        List<ContactRequestVO> list = contactService.listContactRequests(null);
        // Assert
        boolean flag1 = list.stream().anyMatch(o -> o.getId().equals(mock1.getId()));
        boolean flag2 = list.stream().anyMatch(o -> o.getId().equals(mock2.getId()));
        Assertions.assertTrue(flag1);
        Assertions.assertTrue(flag2);
    }
}
