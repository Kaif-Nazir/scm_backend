package com.smartcontactmanager.smart_contact_manager_backend.DTO.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendVerificationEmailRequest(
        @Email @NotBlank String email
) {
}
