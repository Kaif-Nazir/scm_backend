package com.smartcontactmanager.smart_contact_manager_backend.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PasswordResetOtp {

    @Id
    private String otp;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    private LocalDateTime expiry;
}
