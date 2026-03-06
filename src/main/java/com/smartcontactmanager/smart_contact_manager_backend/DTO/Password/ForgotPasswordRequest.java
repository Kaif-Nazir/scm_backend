package com.smartcontactmanager.smart_contact_manager_backend.DTO.Password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @Email @NotBlank String email
) {
}
