package io.leego.chat;

import io.leego.chat.constant.MessageType;
import io.leego.chat.constant.UserType;
import io.leego.chat.dto.MessageCreateDTO;
import io.leego.chat.entity.Message;
import io.leego.chat.exception.ForbiddenException;
import io.leego.chat.vo.MessagePrimeVO;
import io.leego.chat.vo.MessageVO;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Leego Yih
 */
@Rollback
@SpringBootTest
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MessageSenderTests extends ChatTests {
    private static final Logger logger = LoggerFactory.getLogger(MessageSenderTests.class);
    public static final short USER_TYPE = UserType.SENDER.getCode();

    @BeforeEach
    public void before() {
        initAsSender();
    }

    @AfterEach
    public void after() {
        SecurityUtils.remove();
    }

    @Test
    public void testCreateMessage() {
        MessageCreateDTO dto = new MessageCreateDTO(RECIPIENT);
        dto.setType(MessageType.TEXT.getCode());
        dto.setContent("Hello, World!");
        MessagePrimeVO res = messageService.createMessage(dto);
        logger.debug("Create: {}", res);
        // Assert
        Assertions.assertNotNull(res);
        Assertions.assertNotNull(res.getId());
    }

    @Test
    public void testCreateMessageNoContact() {
        contactService.removeContact(SENDER);
        MessageCreateDTO dto = new MessageCreateDTO(RECIPIENT);
        dto.setType(MessageType.TEXT.getCode());
        dto.setContent("Hello, World!");
        try {
            MessagePrimeVO res = messageService.createMessage(dto);
        } catch (ForbiddenException e) {
            // Assert
            logger.debug("Expected");
            return;
        }
        Assertions.fail("Should not be created");
    }

    @Test
    public void testGetMessage() {
        Message mock = mockMessage();
        MessageVO res = messageService.getMessage(mock.getId());
        logger.debug("Get: {}", res);
        // Assert
        Assertions.assertNotNull(res);
        Assertions.assertEquals(mock.getId(), res.getId());
    }

    @Test
    public void testListMessagesAppend() {
        resetMessageStamp(USER_TYPE);
        int size = BATCH_SIZE + 1;
        for (int i = 0; i < 5; i++) {
            mockMessages(size, Instant.now().plusSeconds(i + 10));
        }
        List<MessageVO> oldList = new ArrayList<>();
        while (true) {
            Long id = oldList.isEmpty() ? null : oldList.get(oldList.size() - 1).getId();
            List<MessageVO> newList = messageService.listMessages(id, USER_TYPE);
            if (newList.isEmpty()) {
                break;
            }
            logger.debug("List: id={}, size={}, list={}", id, newList.size(), newList);
            Assertions.assertEquals(size, newList.size());
            oldList.addAll(newList);
        }
        // Assert
        List<Message> os = getMessages(oldList.stream().map(MessageVO::getId).toList());
        for (Message o : os) {
            logger.debug("Take: {}", o);
            MessageVO vo = messageService.getMessage(o.getId());
            Assertions.assertFalse(vo.isTaken());
            Assertions.assertFalse(vo.isSeen());
            Assertions.assertFalse(vo.isRevoked());
            Assertions.assertNull(o.getTakenTime());
            Assertions.assertNull(o.getSeenTime());
            Assertions.assertNull(o.getRevokedTime());
        }
    }

    @Test
    public void testListMessagesRemove() {
        resetMessageStamp(USER_TYPE);
        int size = BATCH_SIZE - 1;
        for (int i = 0; i < 5; i++) {
            mockMessages(size, Instant.now().plusSeconds(i + 10));
        }
        List<MessageVO> oldList = new ArrayList<>();
        while (true) {
            Long id = oldList.isEmpty() ? null : oldList.get(oldList.size() - 1).getId();
            List<MessageVO> newList = messageService.listMessages(id, USER_TYPE);
            if (newList.isEmpty()) {
                break;
            }
            logger.debug("List: id={}, size={}, list={}", id, newList.size(), newList);
            Assertions.assertEquals(size, newList.size());
            oldList.addAll(newList);
        }
        // Assert
        List<Message> os = getMessages(oldList.stream().map(MessageVO::getId).toList());
        for (Message o : os) {
            logger.debug("Take: {}", o);
            MessageVO vo = messageService.getMessage(o.getId());
            Assertions.assertFalse(vo.isTaken());
            Assertions.assertFalse(vo.isSeen());
            Assertions.assertFalse(vo.isRevoked());
            Assertions.assertNull(o.getTakenTime());
            Assertions.assertNull(o.getSeenTime());
            Assertions.assertNull(o.getRevokedTime());
        }
    }

    @Test
    public void testRevokeMessage() {
        Message mock = mockMessage();
        messageService.revokeMessage(mock.getId());
        // Assert
        MessageVO vo = messageService.getMessage(mock.getId());
        Assertions.assertTrue(vo.isRevoked());
        Message o = getMessage(mock.getId());
        Assertions.assertNotNull(o);
        Assertions.assertNull(o.getTakenTime());
        Assertions.assertNull(o.getSeenTime());
        Assertions.assertNotNull(o.getRevokedTime());
        logger.debug("Revoke: {}", o);
    }
}
