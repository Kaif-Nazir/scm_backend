package com.smartcontactmanager.smart_contact_manager_backend.DTO.Contact;

import com.smartcontactmanager.smart_contact_manager_backend.DTO.SocialLink.SocialLinkResponse;

import java.util.List;

public record UpdateContactRequest(
        String name,
        String phoneNumber,
        String email,
        String address,
        String description,
        String linkedInLink,
        Boolean favourite,
        List<SocialLinkResponse> socialLinks
) {}
