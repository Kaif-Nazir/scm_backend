package com.smartcontactmanager.smart_contact_manager_backend.DTO.Password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordWithOtpRequest(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 4, max = 4) String otp,
        @NotBlank @Size(min = 6) String newPassword
) {
}
