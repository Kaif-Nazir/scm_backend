package com.smartcontactmanager.smart_contact_manager_backend.Controller;

import com.smartcontactmanager.smart_contact_manager_backend.DTO.Password.ChangePasswordRequest;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.Password.ForgotPasswordRequest;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.Password.ResetPasswordWithOtpRequest;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.Password.SetPasswordRequest;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.User.LoginRequest;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.User.RegisterUserRequest;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.User.ResendVerificationEmailRequest;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.User.UpdateUserProfileRequest;
import com.smartcontactmanager.smart_contact_manager_backend.DTO.User.UserResponse;
import com.smartcontactmanager.smart_contact_manager_backend.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getUser")
    private ResponseEntity<UserResponse> getUser() {
        return ResponseEntity.ok(userService.getUserDetails());
    }
    @PostMapping("/createUser")
    private ResponseEntity<RegisterUserRequest> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.loginUser(request));
    }
    @PatchMapping("/users/update")
    public ResponseEntity<UserResponse> updateProfile(@RequestBody UpdateUserProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(request));
    }
    @PatchMapping("/users/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request){
        userService.changePassword(request);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/users/set-password")
    public ResponseEntity<Void> setPassword(@RequestBody @Valid SetPasswordRequest request){
        userService.setPassword(request);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        userService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully");
    }
    @PostMapping("/resend/verify-email")
    public ResponseEntity<String> resendVerificationEmail(@Valid @RequestBody ResendVerificationEmailRequest request) {
        userService.resendVerificationEmail(request.email());
        return ResponseEntity.ok("Email Verification Link Sent Check Email");
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        userService.sendPasswordResetOtp(request.email());
        return ResponseEntity.ok("If account exists, password reset OTP has been sent");
    }
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordWithOtpRequest request) {
        userService.resetPasswordWithOtp(request);
        return ResponseEntity.ok("Password reset successfully");
    }
    @DeleteMapping("deleteUser")
    public ResponseEntity<Void> deleteUser() {
        userService.deleteUser();
        return ResponseEntity.noContent().build();
    }


}
