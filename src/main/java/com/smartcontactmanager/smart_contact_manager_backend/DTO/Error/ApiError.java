package com.smartcontactmanager.smart_contact_manager_backend.DTO.Error;

public record ApiError(
        int status,
        String message,
        long timestamp
) {}
