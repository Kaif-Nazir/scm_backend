package com.smartcontactmanager.smart_contact_manager_backend.Service;

import com.smartcontactmanager.smart_contact_manager_backend.Excpetion.CustomException.EmailException;
import com.smartcontactmanager.smart_contact_manager_backend.Models.EmailVerificationToken;
import com.smartcontactmanager.smart_contact_manager_backend.Models.User;
import com.smartcontactmanager.smart_contact_manager_backend.Repository.EmailVerificationTokenRepository;
import com.smartcontactmanager.smart_contact_manager_backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.security.SecureRandom;

@Service
public class EmailVerificationTokenService {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private AsyncMailService asyncMailService;
    private static final SecureRandom TOKEN_RANDOM = new SecureRandom();
    private static final char[] TOKEN_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private static final int TOKEN_LENGTH = 5;

    @Transactional
    public void createAndSendVerificationEmail(User user) {

        if (user.isEmailVerified()) {
            throw new EmailException("EMAIL_ALREADY_VERIFIED");
        }

        emailVerificationTokenRepository.findByUser(user)
                .ifPresent(existing -> {
                    user.setEmailVerificationToken(null);
                    userRepository.save(user);
                    emailVerificationTokenRepository.delete(existing);
                    emailVerificationTokenRepository.flush();
                    entityManager.clear();
                });

        String token = generateUniqueToken();
        EmailVerificationToken ev = new EmailVerificationToken();
        ev.setToken(token);
        ev.setUser(user);
        ev.setExpiry(LocalDateTime.now().plusMinutes(30));
        user.setEmailVerificationToken(ev);

        emailVerificationTokenRepository.save(ev);

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    asyncMailService.sendEmailVerificationToken(user.getEmail(), token);
                }
            });
        } else {
            asyncMailService.sendEmailVerificationToken(user.getEmail(), token);
        }
    }
    private String generateUniqueToken() {
        // Note: 5 chars is low entropy; collisions possible at scale.
        for (int attempt = 0; attempt < 10; attempt++) {
            String token = randomToken();
            if (!emailVerificationTokenRepository.existsById(token)) {
                return token;
            }
        }
        throw new RuntimeException("TOKEN_GENERATION_FAILED");
    }
    private String randomToken() {
        char[] buf = new char[TOKEN_LENGTH];
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            buf[i] = TOKEN_CHARS[TOKEN_RANDOM.nextInt(TOKEN_CHARS.length)];
        }
        return new String(buf);
    }
    @Transactional
    public void verifyEmail(String token){

        EmailVerificationToken ev = emailVerificationTokenRepository.findById(token)
                .orElseThrow(() -> new RuntimeException("INVALID_TOKEN"));

        if (ev.getExpiry().isBefore(LocalDateTime.now())) {
            emailVerificationTokenRepository.delete(ev);
            throw new RuntimeException("TOKEN_EXPIRED");
        }

        User user = ev.getUser();
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        userRepository.save(user);

        emailVerificationTokenRepository.deleteByUser(user);
    }


}
