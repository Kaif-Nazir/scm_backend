package com.smartcontactmanager.smart_contact_manager_backend.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AsyncMailService {

    private static final Logger log = LoggerFactory.getLogger(AsyncMailService.class);

    private final RestClient restClient;
    private final String resendApiKey;
    private final String fromEmail;

    public AsyncMailService(
            @Value("${app.mail.resend.api-key:}") String resendApiKey,
            @Value("${app.mail.from-email:onboarding@resend.dev}") String fromEmail
    ) {
        this.restClient = RestClient.create("https://api.resend.com");
        this.resendApiKey = resendApiKey;
        this.fromEmail = fromEmail;
    }

    @Async("mailTaskExecutor")
    public void sendPasswordResetOtp(String email, String otp) {
        sendMail(email, "[SCM] Password Reset OTP", buildOtpHtml(otp), "PASSWORD_RESET");
    }

    @Async("mailTaskExecutor")
    public void sendEmailVerificationToken(String email, String token) {
        sendMail(email, "[SCM] Verify Your Email", buildVerificationTokenHtml(token), "EMAIL_VERIFICATION");
    }

    private void sendMail(String to, String subject, String html, String kind) {
        if (resendApiKey == null || resendApiKey.isBlank()) {
            log.error("{}_EMAIL_SEND_FAILED: RESEND_API_KEY missing", kind);
            return;
        }

        try {
            restClient.post()
                    .uri("/emails")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + resendApiKey)
                    .body(new ResendEmailRequest(fromEmail, to, subject, html))
                    .retrieve()
                    .toBodilessEntity();
            log.info("{} email sent to {}", kind, to);
        } catch (Exception ex) {
            log.error("{}_EMAIL_SEND_FAILED for {}", kind, to, ex);
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

    private record ResendEmailRequest(
            String from,
            String to,
            String subject,
            String html
    ) {
    }
}
