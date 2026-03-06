package com.smartcontactmanager.smart_contact_manager_backend.Models;

import com.smartcontactmanager.smart_contact_manager_backend.Providers.LoginProviders;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
    public class User {

        @Id
        private String userId;
        @Column(nullable = false)
        private String name;
        private String email;
        private String password;
        @Column(length = 1000)
        private String profilePic;
        private String phoneNumber;

        // Info

        private boolean enabled = false;
        private boolean emailVerified = false;

        // Log-In
        @OneToOne(mappedBy = "user" , cascade = CascadeType.ALL , orphanRemoval = true)
        private EmailVerificationToken emailVerificationToken;
        @Enumerated(EnumType.STRING)
        private LoginProviders provider = LoginProviders.SELF;
        private String providerUserId;

        @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL , fetch = FetchType.LAZY ,  orphanRemoval = true)
        private List<Contact> contacts = new ArrayList<>();


    }
