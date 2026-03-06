package com.smartcontactmanager.smart_contact_manager_backend.Repository;

import com.smartcontactmanager.smart_contact_manager_backend.Models.Contact;
import com.smartcontactmanager.smart_contact_manager_backend.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {

    List<Contact> findByUser(User user);
    List<Contact> findByUserAndNameContainingIgnoreCase(User user, String name);
    List<Contact> findByUserAndFavourite(User user, boolean favourite);
    Contact findByIdAndUser(String contactId,User user);
    long countByUser(User user);
    long countByUserAndFavourite(User user, boolean favourite);

}
