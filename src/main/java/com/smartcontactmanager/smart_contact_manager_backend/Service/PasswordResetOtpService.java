package com.smartcontactmanager.smart_contact_manager_backend.Service;

import com.smartcontactmanager.smart_contact_manager_backend.Excpetion.CustomException.EmailException;
import com.smartcontactmanager.smart_contact_manager_backend.Models.PasswordResetOtp;
import com.smartcontactmanager.smart_contact_manager_backend.Models.User;
import com.smartcontactmanager.smart_contact_manager_backend.Repository.PasswordResetOtpRepository;
import com.smartcontactmanager.smart_contact_manager_backend.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PasswordResetOtpService {

    private static final SecureRandom OTP_RANDOM = new SecureRandom();
    private static final int OTP_LENGTH = 4;

    private final UserRepository userRepository;
    private final PasswordResetOtpRepository passwordResetOtpRepository;
    private final AsyncMailService asyncMailService;
    private final PasswordEncoder passwordEncoder;

    PasswordResetOtpService(
            UserRepository userRepository,
            PasswordResetOtpRepository passwordResetOtpRepository,
            AsyncMailService asyncMailService,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordResetOtpRepository = passwordResetOtpRepository;
        this.asyncMailService = asyncMailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createAndSendOtp(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        // Do not disclose whether account exists.
        if (userOptional.isEmpty()) {
            return;
        }

        User user = userOptional.get();

        passwordResetOtpRepository.deleteByUser(user);

        String otp = generateUniqueOtp();

        PasswordResetOtp passwordResetOtp = new PasswordResetOtp();
        passwordResetOtp.setOtp(otp);
        passwordResetOtp.setUser(user);
        passwordResetOtp.setExpiry(LocalDateTime.now().plusMinutes(10));
        passwordResetOtpRepository.save(passwordResetOtp);
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    asyncMailService.sendPasswordResetOtp(user.getEmail(), otp);
                }
            });
        } else {
            asyncMailService.sendPasswordResetOtp(user.getEmail(), otp);
        }
    }

    @Transactional
    public void resetPassword(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailException("INVALID_OTP_OR_EMAIL"));

        PasswordResetOtp passwordResetOtp = passwordResetOtpRepository.findById(otp)
                .orElseThrow(() -> new EmailException("INVALID_OTP_OR_EMAIL"));

        if (!passwordResetOtp.getUser().getUserId().equals(user.getUserId())) {
            throw new EmailException("INVALID_OTP_OR_EMAIL");
        }

        if (passwordResetOtp.getExpiry().isBefore(LocalDateTime.now())) {
            passwordResetOtpRepository.deleteByUser(user);
            throw new EmailException("OTP_EXPIRED");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetOtpRepository.deleteByUser(user);
    }

    @Transactional
    public void clearOtpForUser(User user) {
        passwordResetOtpRepository.deleteByUser(user);
    }

    private String generateUniqueOtp() {
        for (int attempt = 0; attempt < 10; attempt++) {
            String otp = randomOtp();
            if (!passwordResetOtpRepository.existsById(otp)) {
                return otp;
            }
        }
        throw new RuntimeException("OTP_GENERATION_FAILED");
    }

    private String randomOtp() {
        int bound = (int) Math.pow(10, OTP_LENGTH);
        int value = OTP_RANDOM.nextInt(bound);
        return String.format("%0" + OTP_LENGTH + "d", value);
    }
}
