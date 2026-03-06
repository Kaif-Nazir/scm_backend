package com.smartcontactmanager.smart_contact_manager_backend.DTO.User;

public record LoginRequest(
        String email,
        String password
) {
}
