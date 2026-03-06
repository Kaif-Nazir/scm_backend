package com.smartcontactmanager.smart_contact_manager_backend.Service;

import com.smartcontactmanager.smart_contact_manager_backend.DTO.Password.ChangePasswordRequest;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.Password.ResetPasswordWithOtpRequest;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.Password.SetPasswordRequest;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.User.LoginRequest;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.User.RegisterUserRequest;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.User.UpdateUserProfileRequest;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.User.UserResponse;
import com.smartcontactmanager.smart_contact_manager_backend.JWT.JwtUtil;
import com.smartcontactmanager.smart_contact_manager_backend.Models.User;
import com.smartcontactmanager.smart_contact_manager_backend.Providers.CurrentUserProvider;
import com.smartcontactmanager.smart_contact_manager_backend.Providers.LoginProviders;
import com.smartcontactmanager.smart_contact_manager_backend.Repository.UserRepository;
import com.smartcontactmanager.smart_contact_manager_backend.Excpetion.CustomException.ForbiddenException;
import com.smartcontactmanager.smart_contact_manager_backend.Excpetion.CustomException.EmailException;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CurrentUserProvider  currentUserProvider;
    private final EmailVerificationTokenService emailVerificationTokenService;
    private final PasswordResetOtpService passwordResetOtpService;

    UserService(UserRepository userRepository , JwtUtil jwtUtil ,
                CurrentUserProvider currentUserProvider ,
                EmailVerificationTokenService emailVerificationTokenService,
                PasswordResetOtpService passwordResetOtpService,
                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.currentUserProvider = currentUserProvider;
        this.emailVerificationTokenService = emailVerificationTokenService;
        this.passwordResetOtpService = passwordResetOtpService;
    }

    public RegisterUserRequest register(RegisterUserRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailException("EMAIL_ALREADY_EXISTS");
        }

        User user = User.builder()
                .userId(UUID.randomUUID().toString())
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phoneNumber(request.phoneNumber())
                .enabled(true)
                .emailVerified(false)
                .provider(LoginProviders.SELF)
                .build();

        userRepository.save(user);
        emailVerificationTokenService.createAndSendVerificationEmail(user);
        return request;
    }
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailException("EMAIL_NOT_FOUND"));
        emailVerificationTokenService.createAndSendVerificationEmail(user);
    }
    public void verifyEmail(String token) {
        emailVerificationTokenService.verifyEmail(token);
    }
    public void sendPasswordResetOtp(String email) {
        passwordResetOtpService.createAndSendOtp(email);
    }
    public void resetPasswordWithOtp(ResetPasswordWithOtpRequest req) {
        passwordResetOtpService.resetPassword(req.email(), req.otp(), req.newPassword());
    }

    public Map<String , Object> loginUser (LoginRequest request) {

        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() ->
                        new ForbiddenException("INVALID_CREDENTIALS"));

        if (user.getPassword() == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ForbiddenException("INVALID_CREDENTIALS");
        }

        if (!user.isEnabled()) {
            throw new ForbiddenException("ACCOUNT_DISABLED");
        }
        if (!user.isEmailVerified()) {
            throw new EmailException("EMAIL_NOT_VERIFIED");
        }

        // JWT AND EMAIL PASSOWRD CHECK OR DIRECT FROM GMAIL / GITHUB

        return buildLoginResponse(user);
    }

    @Transactional
    public Map<String, Object> loginWithGoogle(String email, String name, String picture, String providerUserId) {
        if (email == null || email.isBlank()) {
            throw new ForbiddenException("GOOGLE_EMAIL_NOT_AVAILABLE");
        }

        Optional<User> existingUser = userRepository.findByEmail(email);
        boolean isNewUser = existingUser.isEmpty();

        User user = existingUser.orElseGet(() -> User.builder()
                .userId(UUID.randomUUID().toString())
                .name((name == null || name.isBlank()) ? email.split("@")[0] : name)
                .email(email)
                .password(null)
                .profilePic(picture)
                .enabled(true)
                .emailVerified(true)
                .provider(LoginProviders.GOOGLE)
                .providerUserId(providerUserId)
                .build());

        if (isBlank(user.getName()) && !isBlank(name)) {
            user.setName(name);
        }
        if (isBlank(user.getProfilePic()) && !isBlank(picture)) {
            user.setProfilePic(picture);
        }
        if (isNewUser) {
            user.setEnabled(true);
            user.setEmailVerified(true);
        }
        if (user.getProvider() == null) {
            user.setProvider(LoginProviders.GOOGLE);
        }
        if (isBlank(user.getProviderUserId()) && !isBlank(providerUserId)) {
            user.setProviderUserId(providerUserId);
        }

        userRepository.save(user);
        return buildLoginResponse(user);
    }

    private Map<String, Object> buildLoginResponse(User user) {
        String token = jwtUtil.generateToken(user);

        Map<String, Object> response = new HashMap<>();

        response.put("token", token);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getUserId());
        userMap.put("name", user.getName());
        userMap.put("hasPassword", hasPassword(user));

        response.put("user", userMap);

        return response;
    }
    public UserResponse getUserDetails(){
        User user = currentUserProvider.getCurrentUser();
        return new UserResponse(
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getProfilePic(),
                hasPassword(user)
        );
    }
    @Transactional
    public UserResponse updateProfile(UpdateUserProfileRequest req) {

        User user = currentUserProvider.getCurrentUser();

        if (req.name() != null) {
            user.setName(req.name());
        }

        if (req.phoneNumber() != null) {
            user.setPhoneNumber(req.phoneNumber());
        }

        userRepository.save(user);

        return new UserResponse(
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getProfilePic(),
                hasPassword(user)
        );
    }
    @Transactional
    public void changePassword(ChangePasswordRequest req){

        User user = currentUserProvider.getCurrentUser();
        if (!hasPassword(user)) {
            throw new EmailException("PASSWORD_NOT_SET");
        }

        if (!passwordEncoder.matches(req.currentPassword(), user.getPassword())) {
            try {
                throw new BadRequestException("Current password is incorrect");
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }
        }
        user.setPassword(passwordEncoder.encode(req.newPassword()));
        userRepository.save(user);
    }
    @Transactional
    public void setPassword(SetPasswordRequest req) {
        User user = currentUserProvider.getCurrentUser();

        if (hasPassword(user)) {
            throw new EmailException("PASSWORD_ALREADY_SET");
        }

        user.setPassword(passwordEncoder.encode(req.newPassword()));
        userRepository.save(user);
    }

    private boolean hasPassword(User user) {
        return user.getPassword() != null && !user.getPassword().isBlank();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public void deleteUser() {
        User user = currentUserProvider.getCurrentUser();
        passwordResetOtpService.clearOtpForUser(user);
        userRepository.delete(user);
    }


}
