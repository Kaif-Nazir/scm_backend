package com.smartcontactmanager.smart_contact_manager_backend.DTO.User;

public record UserResponse(
        String name,
        String email,
        String phoneNumber,
        String pictureUrl,
        boolean hasPassword
) {
}
