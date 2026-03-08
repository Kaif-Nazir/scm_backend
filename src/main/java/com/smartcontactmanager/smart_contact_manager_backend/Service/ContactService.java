package com.smartcontactmanager.smart_contact_manager_backend.Service;

import com.smartcontactmanager.smart_contact_manager_backend.DTO.Contact.ContactRequest;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.Contact.ContactResponse;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.Contact.ContactResponseFull;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.Contact.ContactStatsResponse;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.Contact.UpdateContactRequest;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.SocialLink.SocialLinkResponse;
import com.smartcontactmanager.smart_contact_manager_backend.Models.Contact;
import com.smartcontactmanager.smart_contact_manager_backend.Models.SocialLink;
import com.smartcontactmanager.smart_contact_manager_backend.Models.User;
import com.smartcontactmanager.smart_contact_manager_backend.Providers.CurrentUserProvider;
import com.smartcontactmanager.smart_contact_manager_backend.Repository.ContactRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ContactService {
    
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private EntityManager entityManager;


    public ContactResponse createContact(ContactRequest req) {

        String userId = currentUserProvider.getCurrentUserId();
        User userRef = entityManager.getReference(User.class, userId);
        Contact contact = Contact.builder()
                .id(UUID.randomUUID().toString())
                .name(req.name())                 // REQUIRED
                .phoneNumber(req.phoneNumber())   // REQUIRED
                .email(req.email())               // optional → null allowed
                .address(req.address())
                .description(req.description())
                .linkedInLink(req.linkedinLink())
                .favourite(req.favourite() != null ? req.favourite() : false)
                .user(userRef)
                .build();

        Contact saved = contactRepository.save(contact);
        return new ContactResponse(
                saved.getId(),
                saved.getName(),
                saved.getPhoneNumber(),
                saved.isFavourite()
        );
    }
    public List<ContactResponse> getAll() {

        String userId = currentUserProvider.getCurrentUserId();

        return contactRepository
                .findByUserUserId(userId)
                .stream()
                .map(contact -> new ContactResponse(
                        contact.getId(),
                        contact.getName(),
                        contact.getPhoneNumber(),
                        contact.isFavourite()
                )).toList();
    }
    public List<ContactResponse> searchContacts(String keyword) {

        String userId = currentUserProvider.getCurrentUserId();

        if (keyword == null || keyword.isBlank()) {
            return getAll();
        }

        return contactRepository
                .findByUserUserIdAndNameContainingIgnoreCase(userId, keyword)
                .stream()
                .map(contact -> new ContactResponse(
                        contact.getId(),
                        contact.getName(),
                        contact.getPhoneNumber(),
                        contact.isFavourite()
                )).toList();
    }
    public List<ContactResponse> getFavouriteContacts() {

        String userId = currentUserProvider.getCurrentUserId();

        return contactRepository
                .findByUserUserIdAndFavourite(userId, true)
                .stream()
                .map(contact -> new ContactResponse(
                        contact.getId(),
                        contact.getName(),
                        contact.getPhoneNumber(),
                        contact.isFavourite()
                )).toList();
    }

    public ContactStatsResponse getContactStats() {
        String userId = currentUserProvider.getCurrentUserId();
        long totalContacts = contactRepository.countByUserUserId(userId);
        long totalFavourites = contactRepository.countByUserUserIdAndFavourite(userId, true);
        return new ContactStatsResponse(totalContacts, totalFavourites);
    }

    public void updateFavourite(String contactId, boolean value) {

        String userId = currentUserProvider.getCurrentUserId();

        Contact contact = contactRepository
                .findByIdAndUserUserId(contactId, userId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        contact.setFavourite(value);
        contactRepository.save(contact);
    }
    public void delete(String id) {
        String userId = currentUserProvider.getCurrentUserId();
        Contact contact = contactRepository
                .findByIdAndUserUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));
        contactRepository.delete(contact);
    }
    // Full Contact
    @Transactional
    public ContactResponseFull getFullContact(String id) {

        String userId = currentUserProvider.getCurrentUserId();

        Contact contact = contactRepository.findByIdAndUserUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        List<SocialLinkResponse> socialLinks =
                contact.getSocialLinks().stream()
                        .map(link -> new SocialLinkResponse(
                                link.getSocialLink(),
                                link.getTitle()
                        ))
                        .toList();


        return new ContactResponseFull(
                contact.getId(),
                contact.getName(),
                contact.getEmail(),
                contact.getPhoneNumber(),
                contact.getAddress(),
                contact.getDescription(),
                contact.isFavourite(),
                contact.getLinkedInLink(),
                socialLinks
        );
    }
    // Update full
    @Transactional
    public ContactResponseFull updateContact(String contactId, UpdateContactRequest req) {

        String userId = currentUserProvider.getCurrentUserId();

        Contact contact = contactRepository
                .findByIdAndUserUserId(contactId, userId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));


        // ---- simple fields (partial update) ----
        if (req.name() != null) contact.setName(req.name());
        if (req.phoneNumber() != null) contact.setPhoneNumber(req.phoneNumber());
        if (req.email() != null) contact.setEmail(req.email());
        if (req.address() != null) contact.setAddress(req.address());
        if (req.description() != null) contact.setDescription(req.description());
        if (req.linkedInLink() != null) contact.setLinkedInLink(req.linkedInLink());
        if (req.favourite() != null) contact.setFavourite(req.favourite());

        // ---- social links handling (important part) ----
        if (req.socialLinks() != null) {

            // remove old links (orphanRemoval = true will delete them)
            contact.getSocialLinks().clear();

            // add new ones
            for (SocialLinkResponse sl : req.socialLinks()) {
                SocialLink link = new SocialLink();
                link.setTitle(sl.title());
                link.setSocialLink(sl.socialLink());
                link.setContact(contact);
                contact.getSocialLinks().add(link);
            }
        }
        contactRepository.save(contact);
        return getFullContact(contactId);
    }
}
