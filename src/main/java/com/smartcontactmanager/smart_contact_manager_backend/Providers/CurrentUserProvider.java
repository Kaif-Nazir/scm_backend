package com.smartcontactmanager.smart_contact_manager_backend.Providers;

import com.smartcontactmanager.smart_contact_manager_backend.Config.CustomUserDetails;
import com.smartcontactmanager.smart_contact_manager_backend.Models.User;
import com.smartcontactmanager.smart_contact_manager_backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    @Autowired
    private UserRepository userRepository;

    public String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof String userId && !userId.isBlank()) {
            return userId;
        }
        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUserId();
        }

        throw new RuntimeException("Unauthenticated");
    }

    public User getCurrentUser() {
        return userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
