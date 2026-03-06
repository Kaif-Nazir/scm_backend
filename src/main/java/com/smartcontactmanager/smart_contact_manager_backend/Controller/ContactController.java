package com.smartcontactmanager.smart_contact_manager_backend.Controller;

import com.smartcontactmanager.smart_contact_manager_backend.DTO.Contact.ContactRequest;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.Contact.ContactResponse;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.Contact.ContactResponseFull;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.Contact.ContactStatsResponse;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.Contact.UpdateContactRequest;
import com.smartcontactmanager.smart_contact_manager_backend.Service.ContactService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ContactController {

    @Autowired
    private ContactService contactService;

    @GetMapping("/api/getAllContacts")
    private ResponseEntity<List<ContactResponse>> getContacts() {
        return ResponseEntity.ok(contactService.getAll());
    }
    @GetMapping("/api/searchContact")
    private ResponseEntity<List<ContactResponse>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(contactService.searchContacts(keyword));
    }
    @GetMapping("/api/favourite")
    private ResponseEntity<List<ContactResponse>> favouriteContact() {
        return ResponseEntity.ok(contactService.getFavouriteContacts());
    }
    @GetMapping("/api/contactStats")
    private ResponseEntity<ContactStatsResponse> getContactStats() {
        return ResponseEntity.ok(contactService.getContactStats());
    }
    @GetMapping("/api/getFullContact/{id}")
    private ResponseEntity<ContactResponseFull> getFullContact(@PathVariable String id) {
        return ResponseEntity.ok(contactService.getFullContact(id));
    }
    @PostMapping("/api/addContact")
    private ResponseEntity<ContactResponse> addContact(@Valid @RequestBody ContactRequest contactRequest) {
        return ResponseEntity.ok(contactService.createContact(contactRequest));
    }
    @PatchMapping("/api/updatefavourite/{contactId}")
    private void updateFavourite(@PathVariable String contactId ,@RequestParam boolean value ) {
        contactService.updateFavourite(contactId , value);
    }
    @PatchMapping("/api/updateContact/{id}")
    public ResponseEntity<ContactResponseFull> updateContact(@PathVariable String id, @RequestBody UpdateContactRequest request){
        return ResponseEntity.ok(contactService.updateContact(id, request));}
    @DeleteMapping("/api/deleteContact/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable String id) {
        contactService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }


}
