package com.smartcontactmanager.smart_contact_manager_backend.Repository;

import com.smartcontactmanager.smart_contact_manager_backend.Models.EmailVerificationToken;
import com.smartcontactmanager.smart_contact_manager_backend.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, String> {
    Optional<EmailVerificationToken> findByUser(User user);
    void deleteByUser(User user);
}
