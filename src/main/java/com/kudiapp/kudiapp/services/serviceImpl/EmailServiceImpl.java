package com.kudiapp.kudiapp.services.serviceImpl;

import com.kudiapp.kudiapp.services.EmailService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Transactional
    public void sendOtp(String to, String subject, String text) {
        try {
            var message = mailSender.createMimeMessage();
            var helper = new org.springframework.mail.javamail.MimeMessageHelper(message, true, "UTF-8");

            // Wrap the OTP text in HTML
            String html = "<p>Your OTP code is:</p><h2>" + text + "</h2>";

            helper.setFrom(fromEmail);  // Must match spring.mail.username
            helper.setTo(to);
            helper.setSubject(subject != null ? subject : "OTP Verification");
            helper.setText(html, true); // true = HTML content

            mailSender.send(message);
            log.info("OTP sent successfully to {}", to);

        } catch (Exception e) {
            log.error("Failed to send OTP to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }
}
