package io.leego.chat.service.impl;

import io.leego.chat.config.ChatProperties;
import io.leego.chat.constant.ContactStatus;
import io.leego.chat.constant.RequestStatus;
import io.leego.chat.dto.ContactRequestCreateDTO;
import io.leego.chat.entity.Contact;
import io.leego.chat.entity.ContactRequest;
import io.leego.chat.exception.NotAcceptableException;
import io.leego.chat.exception.NotFoundException;
import io.leego.chat.manager.ContactManager;
import io.leego.chat.repository.ContactRepository;
import io.leego.chat.repository.ContactRequestRepository;
import io.leego.chat.service.ContactService;
import io.leego.chat.vo.ContactRequestVO;
import io.leego.chat.vo.ContactVO;
import io.leego.mock.entity.User;
import io.leego.mock.service.UserService;
import io.leego.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Leego Yih
 */
@Service
public class ContactServiceImpl implements ContactService {
    private static final Logger logger = LoggerFactory.getLogger(ContactServiceImpl.class);
    private final ContactRepository contactRepository;
    private final ContactRequestRepository contactRequestRepository;
    private final ContactManager contactManager;
    private final ChatProperties properties;
    private final UserService userService;

    public ContactServiceImpl(
            ContactRepository contactRepository,
            ContactRequestRepository contactRequestRepository,
            ContactManager contactManager,
            ChatProperties properties,
            UserService userService) {
        this.contactRepository = contactRepository;
        this.contactRequestRepository = contactRequestRepository;
        this.contactManager = contactManager;
        this.properties = properties;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void blockContact(Long recipient) {
        Long sender = getUserId();
        Contact contact = contactRepository.findBySenderAndRecipient(sender, recipient);
        if (contact == null) {
            throw new NotFoundException("Cannot find contact [%d] for user [%d]".formatted(recipient, sender));
        }
        if (contact.getStatus() != ContactStatus.ADDED.getCode()) {
            logger.error("Cannot block contact [{}] with status [{}]", contact.getId(), contact.getStatus());
            return;
        }
        // Block the recipient
        short status = ContactStatus.BLOCKED.getCode();
        int updated = contactRepository.updateStatus(contact.getId(), contact.getStatus(), status, Instant.now());
        if (updated <= 0) {
            logger.error("Update contact [{}] status {} -> {} failed", contact.getId(), contact.getStatus(), status);
            return;
        }
        contactManager.removeContact(recipient, sender);
    }

    @Override
    @Transactional
    public void removeContact(Long recipient) {
        Long sender = getUserId();
        Contact contact = contactRepository.findBySenderAndRecipient(sender, recipient);
        if (contact == null) {
            throw new NotFoundException("Cannot find contact [%d] for user [%d]".formatted(recipient, sender));
        }
        if (contact.getStatus() == ContactStatus.REMOVED.getCode()) {
            logger.error("Cannot remove contact [{}]", contact.getId());
            return;
        }
        // Remove both
        short status = ContactStatus.REMOVED.getCode();
        int updated = contactRepository.updateStatus(contact.getId(), contact.getStatus(), status, Instant.now());
        if (updated <= 0) {
            logger.error("Update contact [{}] status {} -> {} failed", contact.getId(), contact.getStatus(), status);
            return;
        }
        Contact another = contactRepository.findBySenderAndRecipient(recipient, sender);
        if (another != null && another.getStatus() == ContactStatus.ADDED.getCode()) {
            contactRepository.updateStatus(another.getId(), another.getStatus(), status, Instant.now());
        }
        contactManager.removeContact(sender, recipient);
        contactManager.removeContact(recipient, sender);
    }

    @Override
    public ContactVO getContact(Long recipient) {
        Long sender = getUserId();
        Contact contact = contactRepository.findBySenderAndRecipient(sender, recipient);
        if (contact == null) {
            throw new NotFoundException("Cannot find contact [%d] for user [%d]".formatted(recipient, sender));
        }
        return toContactVO(contact);
    }

    @Override
    public List<ContactVO> listContacts(Instant lastTime) {
        Long sender = getUserId();
        Pageable pageable = Pageable.ofSize(properties.getContact().getFetchSize());
        List<Contact> contacts = lastTime == null
                ? contactRepository.findBySender(sender, pageable)
                : contactRepository.findBySenderAndUpdatedTimeAfter(sender, lastTime, pageable);
        if (contacts.isEmpty()) {
            return Collections.emptyList();
        }
        return toContactVOs(contacts);
    }

    @Override
    @Transactional
    public void createContactRequest(ContactRequestCreateDTO dto) {
        Long sender = getUserId();
        Long recipient = dto.getRecipient();
        if (contactManager.countContacts(sender) >= properties.getContact().getMaxSize()) {
            throw new NotAcceptableException("Too many contacts");
        }
        if (contactRequestRepository.existsBySenderAndRecipientAndStatus(sender, recipient, RequestStatus.APPLIED.getCode())) {
            logger.error("Duplicate request {} -> {}", sender, recipient);
            return;
        }
        if (contactRepository.existsBySenderAndRecipientAndStatus(sender, recipient, ContactStatus.ADDED.getCode())) {
            logger.error("Contact already exists {} -> {}", sender, recipient);
            return;
        }
        ContactRequest request = new ContactRequest(sender, recipient, dto.getMessage(), RequestStatus.APPLIED.getCode());
        contactRequestRepository.save(request);
    }

    @Override
    @Transactional
    public void acceptContactRequest(Long id) {
        Long recipient = getUserId();
        ContactRequest request = contactRequestRepository.findById(id)
                .filter(o -> recipient.equals(o.getRecipient()))
                .orElseThrow(() -> new NotFoundException("Cannot find request [%d] for user [%d]".formatted(id, recipient)));
        if (request.getStatus() != RequestStatus.APPLIED.getCode()) {
            logger.error("Cannot accept request [{}] with status {}", request.getId(), request.getStatus());
            return;
        }
        short status = RequestStatus.ACCEPTED.getCode();
        int updated = contactRequestRepository.updateStatus(request.getId(), request.getStatus(), status, Instant.now());
        if (updated <= 0) {
            logger.error("Update request [{}] status {} -> {} failed", request.getId(), request.getStatus(), status);
            return;
        }
        logger.debug("Create a pair of contacts {} -> {}", request.getSender(), request.getRecipient());
        saveContact(request.getSender(), request.getRecipient());
        saveContact(request.getRecipient(), request.getSender());
    }

    @Override
    @Transactional
    public void rejectContactRequest(Long id) {
        Long recipient = getUserId();
        ContactRequest request = contactRequestRepository.findById(id)
                .filter(o -> recipient.equals(o.getRecipient()))
                .orElseThrow(() -> new NotFoundException("Cannot find request [%d] for user [%d]".formatted(id, recipient)));
        if (request.getStatus() != RequestStatus.APPLIED.getCode()) {
            logger.error("Cannot reject request [{}] with status {}", request.getId(), request.getStatus());
            return;
        }
        short status = RequestStatus.REJECTED.getCode();
        int updated = contactRequestRepository.updateStatus(request.getId(), request.getStatus(), status, Instant.now());
        if (updated <= 0) {
            logger.error("Update request [{}] status {} -> {} failed", request.getId(), request.getStatus(), status);
        }
    }

    @Override
    public ContactRequestVO getContactRequest(Long id) {
        Long recipient = getUserId();
        ContactRequest request = contactRequestRepository.findById(id)
                .filter(o -> recipient.equals(o.getRecipient()))
                .orElseThrow(() -> new NotFoundException("Cannot find request [%d] for user [%d]".formatted(id, recipient)));
        return toRequestVO(request);
    }

    @Override
    public List<ContactRequestVO> listContactRequests(Instant lastTime) {
        Long recipient = getUserId();
        Pageable pageable = Pageable.ofSize(properties.getContact().getFetchSize());
        List<ContactRequest> requests = lastTime == null
                ? contactRequestRepository.findByRecipient(recipient, pageable)
                : contactRequestRepository.findByRecipientAndUpdatedTimeAfter(recipient, lastTime, pageable);
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }
        return toRequestVOs(requests);
    }

    void saveContact(Long sender, Long recipient) {
        Contact contact = contactRepository.findBySenderAndRecipient(sender, recipient);
        if (contact != null) {
            if (contact.getStatus() == ContactStatus.ADDED.getCode()) {
                logger.warn("Contact already exists {} -> {}", sender, recipient);
                return;
            }
            logger.debug("Delete original contact: {}", contact);
            contactRepository.deleteById(contact.getId());
        }
        contactRepository.save(new Contact(sender, recipient, null, ContactStatus.ADDED.getCode()));
        contactManager.addContact(sender, recipient);
    }

    ContactVO toContactVO(Contact o) {
        User user = userService.getUser(o.getRecipient());
        return new ContactVO(
                o.getId(),
                o.getRecipient(),
                user == null ? null : user.getNickname(),
                user == null ? null : user.getAvatar(),
                o.getAlias(),
                o.getCreatedTime(),
                o.getUpdatedTime());
    }

    List<ContactVO> toContactVOs(List<Contact> list) {
        Map<Long, User> users = userService.getUserMap(list.stream().map(Contact::getRecipient).toList());
        return list.stream()
                .map(o -> {
                    User user = users.get(o.getRecipient());
                    return new ContactVO(
                            o.getId(),
                            o.getRecipient(),
                            user == null ? null : user.getNickname(),
                            user == null ? null : user.getAvatar(),
                            o.getAlias(),
                            o.getCreatedTime(),
                            o.getUpdatedTime());
                })
                .toList();
    }

    ContactRequestVO toRequestVO(ContactRequest o) {
        User user = userService.getUser(o.getSender());
        return new ContactRequestVO(
                o.getId(),
                user == null ? null : user.getNickname(),
                user == null ? null : user.getAvatar(),
                o.getMessage(),
                o.getCreatedTime(),
                o.getUpdatedTime());
    }

    List<ContactRequestVO> toRequestVOs(List<ContactRequest> list) {
        Map<Long, User> users = userService.getUserMap(list.stream().map(ContactRequest::getSender).toList());
        return list.stream()
                .map(o -> {
                    User user = users.get(o.getSender());
                    return new ContactRequestVO(
                            o.getId(),
                            user == null ? null : user.getNickname(),
                            user == null ? null : user.getAvatar(),
                            o.getMessage(),
                            o.getCreatedTime(),
                            o.getUpdatedTime());
                })
                .toList();
    }

    Long getUserId() {
        return SecurityUtils.getUserId();
    }
}
