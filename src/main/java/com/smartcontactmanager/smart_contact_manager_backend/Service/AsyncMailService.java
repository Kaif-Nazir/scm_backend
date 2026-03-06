package com.smartcontactmanager.smart_contact_manager_backend.Service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class AsyncMailService {

    private final JavaMailSender mailSender;

    public AsyncMailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async("mailTaskExecutor")
    public void sendPasswordResetOtp(String email, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setTo(email);
            helper.setSubject("[SCM] Password Reset OTP");
            helper.setText(buildOtpHtml(otp), true);
            mailSender.send(message);
        } catch (MessagingException ex) {
            throw new RuntimeException("PASSWORD_RESET_EMAIL_BUILD_FAILED", ex);
        }
    }

    @Async("mailTaskExecutor")
    public void sendEmailVerificationToken(String email, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setTo(email);
            helper.setSubject("[SCM] Verify Your Email");
            helper.setText(buildVerificationTokenHtml(token), true);
            mailSender.send(message);
        } catch (MessagingException ex) {
            throw new RuntimeException("EMAIL_VERIFICATION_BUILD_FAILED", ex);
        }
    }

    private String buildOtpHtml(String otp) {
        StringBuilder boxes = new StringBuilder();
        for (char ch : otp.toCharArray()) {
            boxes.append("<span style=\"display:inline-block;width:44px;height:52px;line-height:52px;")
                    .append("margin:0 6px;border:2px solid #d1d5db;border-radius:10px;")
                    .append("font-size:26px;font-weight:700;color:#111827;background:#f9fafb;\">")
                    .append(ch)
                    .append("</span>");
        }

        return """
                <!doctype html>
                <html>
                <body style="margin:0;padding:24px;background:#f3f4f6;font-family:'Segoe UI',Arial,sans-serif;">
                  <div style="max-width:560px;margin:0 auto;background:#ffffff;border-radius:16px;padding:0;border:1px solid #e5e7eb;overflow:hidden;">
                    <div style="padding:18px 24px;background:linear-gradient(120deg,#0f172a,#1f2937);">
                      <span style="display:inline-block;padding:6px 12px;border-radius:999px;background:#f59e0b;color:#111827;font-size:12px;font-weight:800;letter-spacing:.08em;">SCM</span>
                      <p style="margin:10px 0 0;color:#e5e7eb;font-size:12px;letter-spacing:.03em;">SMART CONTACT MANAGER</p>
                    </div>
                    <div style="padding:24px;">
                    <h2 style="margin:0 0 8px;color:#111827;font-size:22px;">Reset your password</h2>
                    <p style="margin:0 0 18px;color:#374151;font-size:15px;line-height:1.6;">
                      Use this OTP to reset your password. It expires in 10 minutes.
                    </p>
                    <div style="text-align:center;margin:18px 0 20px;">
                      %s
                    </div>
                    <p style="margin:0;color:#6b7280;font-size:13px;line-height:1.6;">
                      If you did not request this, you can safely ignore this email.
                    </p>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(boxes);
    }

    private String buildVerificationTokenHtml(String token) {
        StringBuilder boxes = new StringBuilder();
        for (char ch : token.toCharArray()) {
            boxes.append("<span style=\"display:inline-block;width:34px;height:42px;line-height:42px;")
                    .append("margin:0 3px;border:1px solid #d1d5db;border-radius:8px;")
                    .append("font-size:20px;font-weight:700;color:#111827;background:#f9fafb;\">")
                    .append(ch)
                    .append("</span>");
        }

        return """
                <!doctype html>
                <html>
                <body style="margin:0;padding:24px;background:#f3f4f6;font-family:'Segoe UI',Arial,sans-serif;">
                  <div style="max-width:560px;margin:0 auto;background:#ffffff;border-radius:16px;padding:0;border:1px solid #e5e7eb;overflow:hidden;">
                    <div style="padding:18px 24px;background:linear-gradient(120deg,#0f172a,#1f2937);">
                      <span style="display:inline-block;padding:6px 12px;border-radius:999px;background:#f59e0b;color:#111827;font-size:12px;font-weight:800;letter-spacing:.08em;">SCM</span>
                      <p style="margin:10px 0 0;color:#e5e7eb;font-size:12px;letter-spacing:.03em;">SMART CONTACT MANAGER</p>
                    </div>
                    <div style="padding:24px;">
                    <h2 style="margin:0 0 8px;color:#111827;font-size:22px;">Verify your email</h2>
                    <p style="margin:0 0 14px;color:#374151;font-size:14px;line-height:1.6;">
                      Use this verification code to verify your email address.
                    </p>
                    <div style="text-align:center;margin:14px 0 16px;">
                      %s
                    </div>
                    <p style="margin:0;color:#6b7280;font-size:13px;line-height:1.6;">
                      This code expires in 30 minutes.
                    </p>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(boxes);
    }
}
