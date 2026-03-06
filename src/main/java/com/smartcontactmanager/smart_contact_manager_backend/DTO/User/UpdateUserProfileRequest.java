package com.smartcontactmanager.smart_contact_manager_backend.DTO.User;

public record UpdateUserProfileRequest(
        String name,
        String phoneNumber
) {}

