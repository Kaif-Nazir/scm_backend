package com.smartcontactmanager.smart_contact_manager_backend.Repository;

import com.smartcontactmanager.smart_contact_manager_backend.Models.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {

    List<Contact> findByUserUserId(String userId);
    List<Contact> findByUserUserIdAndNameContainingIgnoreCase(String userId, String name);
    List<Contact> findByUserUserIdAndFavourite(String userId, boolean favourite);
    Optional<Contact> findByIdAndUserUserId(String contactId, String userId);
    long countByUserUserId(String userId);
    long countByUserUserIdAndFavourite(String userId, boolean favourite);

}
