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


    public ContactResponse createContact(ContactRequest req) {

        User user = currentUserProvider.getCurrentUser();
        String id = UUID.randomUUID().toString();
        Contact contact = Contact.builder()
                .id(id)
                .name(req.name())                 // REQUIRED
                .phoneNumber(req.phoneNumber())   // REQUIRED
                .email(req.email())               // optional → null allowed
                .address(req.address())
                .description(req.description())
                .linkedInLink(req.linkedinLink())
                .favourite(req.favourite() != null ? req.favourite() : false)
                .user(user)
                .build();

        contactRepository.save(contact);
        return contactRepository.findById(id).map(con -> new ContactResponse(
                con.getId(),
                con.getName(),
                con.getPhoneNumber(),
                con.isFavourite()
        )).orElseThrow(RuntimeException::new);
    }
    public List<ContactResponse> getAll() {

        User user = currentUserProvider.getCurrentUser();

        return contactRepository
                .findByUser(user)
                .stream()
                .map(contact -> new ContactResponse(
                        contact.getId(),
                        contact.getName(),
                        contact.getPhoneNumber(),
                        contact.isFavourite()
                )).toList();
    }
    public List<ContactResponse> searchContacts(String keyword) {

        User user = currentUserProvider.getCurrentUser();

        if (keyword == null || keyword.isBlank()) {
            return getAll();
        }

        return contactRepository
                .findByUserAndNameContainingIgnoreCase(user, keyword)
                .stream()
                .map(contact -> new ContactResponse(
                        contact.getId(),
                        contact.getName(),
                        contact.getPhoneNumber(),
                        contact.isFavourite()
                )).toList();
    }
    public List<ContactResponse> getFavouriteContacts() {

        User user = currentUserProvider.getCurrentUser();

        return contactRepository
                .findByUserAndFavourite(user, true)
                .stream()
                .map(contact -> new ContactResponse(
                        contact.getId(),
                        contact.getName(),
                        contact.getPhoneNumber(),
                        contact.isFavourite()
                )).toList();
    }

    public ContactStatsResponse getContactStats() {
        User user = currentUserProvider.getCurrentUser();
        long totalContacts = contactRepository.countByUser(user);
        long totalFavourites = contactRepository.countByUserAndFavourite(user, true);
        return new ContactStatsResponse(totalContacts, totalFavourites);
    }

    public void updateFavourite(String contactId, boolean value) {

        User user = currentUserProvider.getCurrentUser();

        Contact contact = contactRepository
                .findById(contactId)
                .orElseThrow();

        // ownership check
        if (!contact.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }

        contact.setFavourite(value);
        contactRepository.save(contact);
    }
    public void delete(String id) {
        User user = currentUserProvider.getCurrentUser();
        Contact contact = contactRepository.findById(id).orElseThrow(() -> new RuntimeException("Contact not found"));

        if(contact.getUser().getUserId().equals(user.getUserId())) {
            contactRepository.deleteById(id);
        }
    }
    // Full Contact
    @Transactional
    public ContactResponseFull getFullContact(String id) {

        User user = currentUserProvider.getCurrentUser();

        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        if (!contact.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized access");
        }

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

        User user = currentUserProvider.getCurrentUser();

        Contact contact = contactRepository
                .findByIdAndUser(contactId, user);


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
