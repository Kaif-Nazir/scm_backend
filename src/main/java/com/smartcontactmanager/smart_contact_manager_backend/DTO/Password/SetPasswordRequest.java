package com.smartcontactmanager.smart_contact_manager_backend.DTO.Password;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SetPasswordRequest(
        @NotBlank @Size(min = 6) String newPassword
) {
}
