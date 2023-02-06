package io.leego.chat.controller;

import io.leego.chat.dto.ContactRequestCreateDTO;
import io.leego.chat.exception.NotAcceptableException;
import io.leego.chat.exception.NotFoundException;
import io.leego.chat.service.ContactService;
import io.leego.chat.vo.ContactRequestVO;
import io.leego.chat.vo.ContactVO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

/**
 * @author Leego Yih
 */
@RestController
@RequestMapping("contacts")
public class ContactController {
    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    /**
     * Blocks the contact with the given recipient.
     *
     * @param recipient must not be {@literal null}.
     * @throws NotFoundException if the contact is not found.
     */
    @PatchMapping("{recipient}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void blockContact(@PathVariable Long recipient) {
        contactService.blockContact(recipient);
    }

    /**
     * Removes the contact with the given recipient.
     *
     * @param recipient must not be {@literal null}.
     * @throws NotFoundException if the contact is not found.
     */
    @DeleteMapping("{recipient}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeContact(@PathVariable Long recipient) {
        contactService.removeContact(recipient);
    }

    /**
     * Returns the contact with the given recipient.
     *
     * @param recipient must not be {@literal null}.
     * @return the contact request with the given ID.
     * @throws NotFoundException if the contact is not found.
     */
    @GetMapping("{recipient}")
    @ResponseStatus(HttpStatus.OK)
    public ContactVO getContact(@PathVariable Long recipient) {
        return contactService.getContact(recipient);
    }

    /**
     * Returns all contacts of the current user.
     *
     * @param lastTime could be {@literal null}.
     * @return all contacts of the current user.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ContactVO> listContacts(@RequestParam(required = false) Instant lastTime) {
        return contactService.listContacts(lastTime);
    }

    /**
     * Creates a contact request.
     *
     * @param contact must not be {@literal null}.
     * @throws NotAcceptableException if too many contacts.
     */
    @PostMapping("requests")
    @ResponseStatus(HttpStatus.CREATED)
    public void createContactRequest(@Validated @RequestBody ContactRequestCreateDTO contact) {
        contactService.createContactRequest(contact);
    }

    /**
     * Accepts the contact request with the given ID.
     *
     * @param id must not be {@literal null}.
     * @throws NotFoundException if the contact request is not found, or does not belong to the user.
     */
    @PatchMapping(value = "requests/{id}", params = "action=accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptContactRequest(@PathVariable Long id) {
        contactService.acceptContactRequest(id);
    }

    /**
     * Rejects the contact request with the given ID.
     *
     * @param id must not be {@literal null}.
     * @throws NotFoundException if the contact request is not found, or does not belong to the user.
     */
    @PatchMapping(value = "requests/{id}", params = "action=reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectContactRequest(@PathVariable Long id) {
        contactService.rejectContactRequest(id);
    }

    /**
     * Returns the contact request with the given ID.
     *
     * @param id must not be {@literal null}.
     * @return the contact request with the given ID.
     * @throws NotFoundException if the contact request is not found, or does not belong to the user.
     */
    @GetMapping("requests/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ContactRequestVO getContactRequest(@PathVariable Long id) {
        return contactService.getContactRequest(id);
    }

    /**
     * Returns all contact requests of the current user.
     *
     * @param lastTime could be {@literal null}.
     * @return all contact requests of the current user.
     */
    @GetMapping("requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ContactRequestVO> listContactRequests(@RequestParam(required = false) Instant lastTime) {
        return contactService.listContactRequests(lastTime);
    }

}
