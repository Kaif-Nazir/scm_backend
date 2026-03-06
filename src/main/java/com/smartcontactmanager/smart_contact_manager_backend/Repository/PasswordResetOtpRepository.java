package com.smartcontactmanager.smart_contact_manager_backend.Repository;

import com.smartcontactmanager.smart_contact_manager_backend.Models.PasswordResetOtp;
import com.smartcontactmanager.smart_contact_manager_backend.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, String> {
    Optional<PasswordResetOtp> findByUser(User user);
    void deleteByUser(User user);
}
