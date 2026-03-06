package com.smartcontactmanager.smart_contact_manager_backend.DTO.Contact;

public record ContactResponse(
        String id,
        String name,
        String phoneNumber,
        boolean favourite
) {}
