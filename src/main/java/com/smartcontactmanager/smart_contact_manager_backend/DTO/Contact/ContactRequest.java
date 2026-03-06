package com.smartcontactmanager.smart_contact_manager_backend.DTO.Contact;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactRequest (

        @NotBlank String name,
        @NotBlank @Size(min = 10 , max = 10) String phoneNumber,
        String email,
        String address,
        String description,
        Boolean favourite,
        String linkedinLink
)
{}
