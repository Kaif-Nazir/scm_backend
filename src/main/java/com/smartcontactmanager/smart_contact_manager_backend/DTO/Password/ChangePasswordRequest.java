package com.smartcontactmanager.smart_contact_manager_backend.DTO.Password;

import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        String currentPassword ,
        @Size(min = 6)String newPassword
) {
}
