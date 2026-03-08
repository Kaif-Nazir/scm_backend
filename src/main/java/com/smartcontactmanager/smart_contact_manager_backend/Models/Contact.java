package com.smartcontactmanager.smart_contact_manager_backend.Models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "contact",
        indexes = {
                @Index(name = "idx_contact_user_user_id", columnList = "user_user_id"),
                @Index(name = "idx_contact_user_user_id_favourite", columnList = "user_user_id,favourite"),
                @Index(name = "idx_contact_user_user_id_name", columnList = "user_user_id,name")
        }
)
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
    @JoinColumn(name = "user_user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<SocialLink> socialLinks = new ArrayList<>();
}
