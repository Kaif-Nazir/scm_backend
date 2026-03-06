package com.smartcontactmanager.smart_contact_manager_backend.DTO.Contact;



import com.smartcontactmanager.smart_contact_manager_backend.DTO.SocialLink.SocialLinkResponse;

import java.util.List;

public record ContactResponseFull(
        String id,
        String name,
        String email,
        String phoneNumber,
        String address,
        String description,
        boolean favourite,
        String linkedInLink,
        List<SocialLinkResponse> socialLinksResponse
){}
