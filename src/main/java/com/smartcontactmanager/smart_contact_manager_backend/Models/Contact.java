package com.smartcontactmanager.smart_contact_manager_backend.Models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Contact {

    @Id
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    @Column(length = 1000)
    private String description;
    private boolean favourite = false;
    private String linkedInLink;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "contact" , cascade = CascadeType.ALL , fetch = FetchType.EAGER ,  orphanRemoval = true)
    private List<SocialLink> socialLinks = new ArrayList<>();
}
