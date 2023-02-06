package io.leego.chat;

import io.leego.chat.constant.UserType;
import io.leego.chat.entity.Message;
import io.leego.chat.exception.NotFoundException;
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
public class MessageRecipientTests extends ChatTests {
    private static final Logger logger = LoggerFactory.getLogger(MessageRecipientTests.class);
    public static final short USER_TYPE = UserType.RECIPIENT.getCode();

    @BeforeEach
    public void before() {
        initAsRecipient();
    }

    @AfterEach
    public void after() {
        SecurityUtils.remove();
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
    public void testGetMessageNotMine() {
        Message mock = mockMessage(SENDER, ANYONE);
        try {
            MessageVO res = messageService.getMessage(mock.getId());
        } catch (NotFoundException e) {
            // Assert
            logger.debug("Expected not found");
            return;
        }
        Assertions.fail("Should not be found");
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
            Assertions.assertTrue(vo.isTaken());
            Assertions.assertFalse(vo.isSeen());
            Assertions.assertFalse(vo.isRevoked());
            Assertions.assertNotNull(o.getTakenTime());
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
            Assertions.assertTrue(vo.isTaken());
            Assertions.assertFalse(vo.isSeen());
            Assertions.assertFalse(vo.isRevoked());
            Assertions.assertNotNull(o.getTakenTime());
            Assertions.assertNull(o.getSeenTime());
            Assertions.assertNull(o.getRevokedTime());
        }
    }

    @Test
    public void testTakeOneMessage() {
        Message mock = mockMessage();
        messageService.takeMessages(new Long[]{mock.getId()});
        // Assert
        MessageVO vo = messageService.getMessage(mock.getId());
        Assertions.assertTrue(vo.isTaken());
        Assertions.assertFalse(vo.isSeen());
        Assertions.assertFalse(vo.isRevoked());
        Message o = getMessage(mock.getId());
        Assertions.assertNotNull(o.getTakenTime());
        Assertions.assertNull(o.getSeenTime());
        Assertions.assertNull(o.getRevokedTime());
        logger.debug("Take: {}", o);
    }

    @Test
    public void testTakeSeveralMessages() {
        List<Message> mock = mockMessages(BATCH_SIZE);
        messageService.takeMessages(mock.stream().map(Message::getId).toArray(Long[]::new));
        // Assert
        List<Message> os = getMessages(mock.stream().map(Message::getId).toList());
        for (Message o : os) {
            MessageVO vo = messageService.getMessage(o.getId());
            Assertions.assertTrue(vo.isTaken());
            Assertions.assertFalse(vo.isSeen());
            Assertions.assertFalse(vo.isRevoked());
            Assertions.assertNotNull(o.getTakenTime());
            Assertions.assertNull(o.getSeenTime());
            Assertions.assertNull(o.getRevokedTime());
            logger.debug("Take: {}", o);
        }
    }

    @Test
    public void testTakeAfterRead() {
        List<Message> mock = mockMessages(4);
        messageService.readMessages(mock.stream().map(Message::getId).limit(2).toArray(Long[]::new));
        // Assert
        for (int i = 0; i < 2; i++) {
            Message o = getMessage(mock.get(i).getId());
            MessageVO vo = messageService.getMessage(o.getId());
            Assertions.assertTrue(vo.isTaken());
            Assertions.assertTrue(vo.isSeen());
            Assertions.assertFalse(vo.isRevoked());
            Assertions.assertNull(o.getTakenTime());
            Assertions.assertNotNull(o.getSeenTime());
            Assertions.assertNull(o.getRevokedTime());
            logger.debug("Read first: {}", o);
        }
        for (int i = 2; i < 4; i++) {
            Message o = getMessage(mock.get(i).getId());
            MessageVO vo = messageService.getMessage(o.getId());
            Assertions.assertFalse(vo.isTaken());
            Assertions.assertFalse(vo.isSeen());
            Assertions.assertFalse(vo.isRevoked());
            Assertions.assertNull(o.getTakenTime());
            Assertions.assertNull(o.getSeenTime());
            Assertions.assertNull(o.getRevokedTime());
            logger.debug("No change: {}", o);
        }
        messageService.takeMessages(mock.stream().map(Message::getId).limit(4).toArray(Long[]::new));
        // Assert
        for (int i = 0; i < 2; i++) {
            Message o = getMessage(mock.get(i).getId());
            MessageVO vo = messageService.getMessage(o.getId());
            Assertions.assertTrue(vo.isTaken());
            Assertions.assertTrue(vo.isSeen());
            Assertions.assertFalse(vo.isRevoked());
            Assertions.assertNull(o.getTakenTime());
            Assertions.assertNotNull(o.getSeenTime());
            Assertions.assertNull(o.getRevokedTime());
            logger.debug("Take after Read: {}", o);
        }
        for (int i = 2; i < 4; i++) {
            Message o = getMessage(mock.get(i).getId());
            MessageVO vo = messageService.getMessage(o.getId());
            Assertions.assertTrue(vo.isTaken());
            Assertions.assertFalse(vo.isSeen());
            Assertions.assertFalse(vo.isRevoked());
            Assertions.assertNotNull(o.getTakenTime());
            Assertions.assertNull(o.getSeenTime());
            Assertions.assertNull(o.getRevokedTime());
            logger.debug("Take only: {}", o);
        }
    }

    @Test
    public void testReadOneMessage() {
        Message mock = mockMessage();
        messageService.readMessages(new Long[]{mock.getId()});
        // Assert
        MessageVO vo = messageService.getMessage(mock.getId());
        Assertions.assertTrue(vo.isTaken());
        Assertions.assertTrue(vo.isSeen());
        Assertions.assertFalse(vo.isRevoked());
        Message o = getMessage(mock.getId());
        Assertions.assertNull(o.getTakenTime());
        Assertions.assertNotNull(o.getSeenTime());
        Assertions.assertNull(o.getRevokedTime());
        logger.debug("Read: {}", o);
    }

    @Test
    public void testReadSeveralMessages() {
        List<Message> mock = mockMessages(BATCH_SIZE);
        messageService.readMessages(mock.stream().map(Message::getId).toArray(Long[]::new));
        // Assert
        List<Message> os = getMessages(mock.stream().map(Message::getId).toList());
        for (Message o : os) {
            MessageVO vo = messageService.getMessage(o.getId());
            Assertions.assertTrue(vo.isTaken());
            Assertions.assertTrue(vo.isSeen());
            Assertions.assertFalse(vo.isRevoked());
            Assertions.assertNull(o.getTakenTime());
            Assertions.assertNotNull(o.getSeenTime());
            Assertions.assertNull(o.getRevokedTime());
            logger.debug("Read: {}", o);
        }
    }

    @Test
    public void testReadAfterTake() {
        List<Message> mock = mockMessages(4);
        messageService.takeMessages(mock.stream().map(Message::getId).limit(2).toArray(Long[]::new));
        // Assert
        for (int i = 0; i < 2; i++) {
            Message o = getMessage(mock.get(i).getId());
            MessageVO vo = messageService.getMessage(o.getId());
            Assertions.assertTrue(vo.isTaken());
            Assertions.assertFalse(vo.isSeen());
            Assertions.assertFalse(vo.isRevoked());
            Assertions.assertNotNull(o.getTakenTime());
            Assertions.assertNull(o.getSeenTime());
            Assertions.assertNull(o.getRevokedTime());
            logger.debug("Take first: {}", o);
        }
        for (int i = 2; i < 4; i++) {
            Message o = getMessage(mock.get(i).getId());
            MessageVO vo = messageService.getMessage(o.getId());
            Assertions.assertFalse(vo.isTaken());
            Assertions.assertFalse(vo.isSeen());
            Assertions.assertFalse(vo.isRevoked());
            Assertions.assertNull(o.getTakenTime());
            Assertions.assertNull(o.getSeenTime());
            Assertions.assertNull(o.getRevokedTime());
            logger.debug("No change: {}", o);
        }
        messageService.readMessages(mock.stream().map(Message::getId).limit(4).toArray(Long[]::new));
        // Assert
        for (int i = 0; i < 2; i++) {
            Message o = getMessage(mock.get(i).getId());
            MessageVO vo = messageService.getMessage(o.getId());
            Assertions.assertTrue(vo.isTaken());
            Assertions.assertTrue(vo.isSeen());
            Assertions.assertFalse(vo.isRevoked());
            Assertions.assertNotNull(o.getTakenTime());
            Assertions.assertNotNull(o.getSeenTime());
            Assertions.assertNull(o.getRevokedTime());
            logger.debug("Read after Take: {}", o);
        }
        for (int i = 2; i < 4; i++) {
            Message o = getMessage(mock.get(i).getId());
            MessageVO vo = messageService.getMessage(o.getId());
            Assertions.assertTrue(vo.isTaken());
            Assertions.assertTrue(vo.isSeen());
            Assertions.assertFalse(vo.isRevoked());
            Assertions.assertNull(o.getTakenTime());
            Assertions.assertNotNull(o.getSeenTime());
            Assertions.assertNull(o.getRevokedTime());
            logger.debug("Read only: {}", o);
        }
    }
}
