package com.trace360.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.from-name}")
    private String fromName;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendOtpEmail(String toEmail, String otp, int attemptsRemaining) {
        String subject = "Trace360 — Your Email Verification OTP";
        String body = "<div style='font-family:Arial,sans-serif;padding:32px;'>"
            + "<h2>Your OTP Code</h2>"
            + "<div style='font-size:40px;font-weight:700;letter-spacing:12px;color:#00c8ff;padding:20px;'>" + otp + "</div>"
            + "<p>Valid for 10 minutes. Attempts remaining: " + attemptsRemaining + "</p>"
            + "</div>";
        sendHtmlEmail(toEmail, subject, body);
    }

    @Async
    public void sendTrackingEmail(String toEmail, String receiverName, String trackingId) {
        String trackingUrl = frontendUrl + "/track/" + trackingId;
        String subject = "Your Package is on the Way! Tracking ID: " + trackingId;
        String body = "<div style='font-family:Arial,sans-serif;padding:32px;'>"
            + "<h2>Hello, " + receiverName + "!</h2>"
            + "<p>Your package <b>" + trackingId + "</b> has been registered.</p>"
            + "<a href='" + trackingUrl + "'>Track My Package</a>"
            + "</div>";
        sendHtmlEmail(toEmail, subject, body);
        log.info("Tracking email sent to {} for {}", toEmail, trackingId);
    }

    @Async
    public void sendStatusUpdateEmail(String toEmail, String receiverName,
                                       String trackingId, String status) {
        String trackingUrl = frontendUrl + "/track/" + trackingId;
        String subject = "Trace360 — Package Update: " + status;
        String body = "<div style='font-family:Arial,sans-serif;padding:32px;'>"
            + "<h2>Hello " + receiverName + "!</h2>"
            + "<p>Your package <b>" + trackingId + "</b> status: <b>" + status + "</b></p>"
            + "<a href='" + trackingUrl + "'>View Live Tracking</a>"
            + "</div>";
        sendHtmlEmail(toEmail, subject, body);
    }

    @Async
    public void sendDeliveryOtpEmail(String toEmail, String receiverName,
                                      String trackingId, String otp) {
        String subject = "Trace360 - Your Delivery Confirmation OTP";
        String body = "<div style='font-family:Arial,sans-serif;padding:32px;'>"
            + "<h2>Hello " + receiverName + "!</h2>"
            + "<p>Your delivery agent is at your door for package <b>" + trackingId + "</b>.</p>"
            + "<p>Share this OTP with the agent to confirm delivery:</p>"
            + "<div style='font-size:40px;font-weight:700;letter-spacing:12px;color:#00c8ff;padding:20px;'>" + otp + "</div>"
            + "<p style='color:#ff4060;'>Do NOT share with anyone other than your delivery agent.</p>"
            + "<p>Valid for 30 minutes.</p></div>";
        sendHtmlEmail(toEmail, subject, body);
        log.info("Delivery OTP email sent to {} for package {}", toEmail, trackingId);
    }

    @Async
    public void sendWelcomeEmail(String toEmail, String name) {
        String subject = "Welcome to Trace360!";
        String body = "<div style='font-family:Arial,sans-serif;padding:32px;'>"
            + "<h2>Welcome, " + name + "!</h2>"
            + "<p>Your account is ready. Track packages in real-time.</p>"
            + "<a href='" + frontendUrl + "'>Get Started</a>"
            + "</div>";
        sendHtmlEmail(toEmail, subject, body);
    }

    private void sendHtmlEmail(String toEmail, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent to {} | {}", toEmail, subject);
        } catch (Exception e) {
            log.error("Email failed to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Email send failed: " + e.getMessage());
        }
    }
}
