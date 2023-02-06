package io.leego.chat;

import io.leego.chat.config.ChatProperties;
import io.leego.chat.constant.ClientType;
import io.leego.chat.constant.DeviceType;
import io.leego.chat.constant.MessageType;
import io.leego.chat.entity.Contact;
import io.leego.chat.entity.ContactRequest;
import io.leego.chat.entity.Message;
import io.leego.chat.manager.ContactManager;
import io.leego.chat.manager.GroupManager;
import io.leego.chat.repository.ContactRepository;
import io.leego.chat.repository.ContactRequestRepository;
import io.leego.chat.repository.GroupMemberRepository;
import io.leego.chat.repository.GroupRepository;
import io.leego.chat.repository.MessageRepository;
import io.leego.chat.repository.MessageStampRepository;
import io.leego.chat.service.ContactService;
import io.leego.chat.service.GroupService;
import io.leego.chat.service.MessageService;
import io.leego.security.Authentication;
import io.leego.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Leego Yih
 */
public abstract class ChatTests {
    private static final Logger logger = LoggerFactory.getLogger(ChatTests.class);
    public static final long SENDER = 1;
    public static final long RECIPIENT = 2;
    public static final long ANYONE = 9999;
    public static final int BATCH_SIZE = 10;
    @Autowired
    public ContactService contactService;
    @Autowired
    public ContactRepository contactRepository;
    @Autowired
    public ContactRequestRepository contactRequestRepository;
    @Autowired
    public MessageService messageService;
    @Autowired
    public MessageRepository messageRepository;
    @Autowired
    public MessageStampRepository messageStampRepository;

    @Autowired
    public GroupService groupService;
    @Autowired
    public GroupRepository groupRepository;
    @Autowired
    public GroupMemberRepository groupMemberRepository;

    @Autowired
    public ContactManager contactManager;
    @Autowired
    public GroupManager groupManager;
    @Autowired
    public ChatProperties properties;

    static {
        System.getProperties().setProperty("chat.message.batch-size", Integer.toString(BATCH_SIZE));
        System.getProperties().setProperty("logging.level.org.hibernate.SQL", "debug");
    }

    public void initAsSender() {
        Authentication a = new Authentication(
                SENDER,
                "sender",
                "sender",
                "no",
                1L,
                DeviceType.MAC.getCode(),
                ClientType.DESKTOP.getCode(),
                SecurityUtils.randomToken());
        SecurityUtils.set(a);
    }

    public void initAsRecipient() {
        Authentication a = new Authentication(
                RECIPIENT,
                "recipient",
                "recipient",
                "no",
                1L,
                DeviceType.MAC.getCode(),
                ClientType.DESKTOP.getCode(),
                SecurityUtils.randomToken());
        SecurityUtils.set(a);
    }

    public Message getMessage(Long id) {
        return messageRepository.findById(id).orElse(null);
    }

    public List<Message> getMessages(List<Long> ids) {
        return messageRepository.findAllById(ids);
    }

    public Message mockMessage() {
        return mockMessage(SENDER, RECIPIENT);
    }

    public Message mockMessage(long sender, long recipient) {
        Instant now = Instant.now();
        Message message = messageRepository.save(new Message(
                sender, recipient, MessageType.TEXT.getCode(), "Hello, World!",
                now, now, null, null, null));
        logger.debug("Mocked: {}", message);
        return message;
    }

    public List<Message> mockMessages(int n) {
        return mockMessages(n, Instant.now());
    }

    public List<Message> mockMessages(int n, Instant t) {
        List<Message> list = messageRepository.saveAll(
                IntStream.range(0, n)
                        .mapToObj(i -> new Message(
                                SENDER, RECIPIENT, MessageType.TEXT.getCode(), "Hello, " + i + "!",
                                t, t, null, null, null))
                        .toList());
        logger.debug("Mock: {}", list);
        return list;
    }

    public void resetMessageStamp(short type) {
        Message message = mockMessage();
        messageService.listMessages(message.getId(), type);
    }

    public Contact mockContact(short status) {
        return mockContact(SENDER, RECIPIENT, status);
    }

    public Contact mockContact(long sender, long recipient, short status) {
        Contact contact = new Contact(sender, recipient, null, status);
        contactRepository.save(contact);
        logger.debug("Mocked: {}", contact);
        return contact;
    }

    public List<Contact> mockContacts(short status, int n) {
        return mockContacts(status, n, Instant.now());
    }

    public List<Contact> mockContacts(short status, int n, Instant t) {
        List<Contact> list = contactRepository.saveAll(
                IntStream.range(0, n)
                        .mapToObj(i -> new Contact(SENDER, RECIPIENT, null, status))
                        .toList());
        logger.debug("Mock: {}", list);
        return list;
    }

    public ContactRequest mockContactRequest(short status) {
        return mockContactRequest(SENDER, RECIPIENT, status);
    }

    public ContactRequest mockContactRequest(long sender, long recipient, short status) {
        ContactRequest request = new ContactRequest(sender, recipient, null, status);
        contactRequestRepository.save(request);
        logger.debug("Mocked: {}", request);
        return request;
    }
}
