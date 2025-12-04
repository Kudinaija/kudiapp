package com.kudiapp.kudiapp.services.serviceImpl;

import com.kudiapp.kudiapp.exceptions.InvalidTokenException;
import com.kudiapp.kudiapp.exceptions.ResourceNotFoundException;
import com.kudiapp.kudiapp.models.VerificationCode;
import com.kudiapp.kudiapp.repository.VerificationCodeRepository;
import com.kudiapp.kudiapp.services.EmailService;
import com.kudiapp.kudiapp.services.SendGridEmailService;
import com.kudiapp.kudiapp.services.VerificationCodeService;
import com.kudiapp.kudiapp.utills.OtpUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final SendGridEmailService sendGridEmailService;
    private final JavaMailSender mailSender;


    public VerificationCodeServiceImpl(VerificationCodeRepository verificationCodeRepository, EmailService emailService, SendGridEmailService sendGridEmailService, JavaMailSender mailSender) {
        this.verificationCodeRepository = verificationCodeRepository;
        this.emailService = emailService;
        this.sendGridEmailService = sendGridEmailService;
        this.mailSender = mailSender;
    }

    @Transactional
    @Override
    public void sendOtpForRegistration(String email) {
        try {
            log.info("Starting OTP generation for userEmail={}", mask(email));

            // 1️⃣ Generate a 6-digit OTP
            String otpCode = OtpUtil.generateSixDigitCode();

            // 2️⃣ Check if an OTP already exists
            VerificationCode existingOtp = verificationCodeRepository.findByEmail(email);

            if (existingOtp == null) {
                log.info("No existing OTP found. Creating new OTP for userEmail={}", mask(email));
                existingOtp = new VerificationCode();
                existingOtp.setEmail(email);
            } else {
                log.info("Existing OTP found. Updating OTP for userEmail={}", mask(email));
            }

            // 3️⃣ Set code and expiry
            existingOtp.setCode(otpCode);
            existingOtp.setExpiresAt(LocalDateTime.now().plusMinutes(10));

            // 4️⃣ Save/update in DB
            verificationCodeRepository.save(existingOtp);

            log.info("OTP saved successfully for userEmail={}. OTP={}", mask(email), otpCode);

            // 5️⃣ Send OTP via email
//            emailService.sendOtp(
//                    email,
//                    "Email Verification Code",
//                    "Your verification code is: " + otpCode + "\nThis code expires in 10 minutes."
//            );
            sendGridEmailService.sendOtp(email, "Your OTP", otpCode);


            log.info("OTP email sent successfully to userEmail={}", mask(email));

        } catch (Exception e) {
            log.error("Failed to generate/send OTP for userEmail={}. error={}", mask(email), e.getMessage(), e);
            throw new RuntimeException("Could not send OTP email", e);
        }
    }


    @Override
    @Transactional
    public void sendOtpCode(String email) {
        try {
            log.info("Starting OTP generation for userEmail={}", mask(email));

            String code = OtpUtil.generateSixDigitCode();
            VerificationCode existing = verificationCodeRepository.findByEmail(email);

            if (existing == null) {
                log.info("No existing OTP entry found. Creating new OTP record for userEmail={}", mask(email));
                existing = new VerificationCode();
                existing.setEmail(email);
            } else {
                log.info("Existing OTP record found. Updating OTP for userEmail={}", mask(email));
            }

            existing.setCode(code);
            existing.setExpiresAt(LocalDateTime.now().plusMinutes(10));
            verificationCodeRepository.save(existing);

            log.info("OTP saved successfully. Sending OTP email to userEmail={}", mask(email));
            log.info("OTP saved successfully. Sending OTP otp={}", existing.getCode());

            emailService.sendOtp(
                    email,
                    "Your Verification Code",
                    "Your verification code is: " + code + "\nThis code expires in 10 minutes."
            );

            log.info("OTP email sent successfully to userEmail={}", mask(email));

        } catch (Exception ex) {
            log.error("Failed to generate or send OTP to userEmail={}. error={}", mask(email), ex.getMessage(), ex);
            throw ex;
        }

    }

    @Override
    @Transactional
    public boolean verifyOtpCode(String email, String code) {
        try {
            log.info("Attempting OTP verification for userEmail={} with submittedCode={}", mask(email), code);

            VerificationCode entry = verificationCodeRepository.findByEmail(email);

            if (entry == null) {
                log.warn("OTP verification failed. No OTP record found for userEmail={}", mask(email));
                throw new ResourceNotFoundException("No OTP record found for userEmail=" + mask(email));
            }

            if (entry.isExpired()) {
                log.warn("OTP verification failed. OTP expired for userEmail={}", mask(email));
                throw new InvalidTokenException("OTP expired for userEmail=" + mask(email));
            }

            if (!entry.getCode().equals(code)) {
                log.warn("OTP verification failed. Incorrect code for userEmail={}", mask(email));
                throw new InvalidTokenException("Incorrect code for userEmail=" + mask(email));
            }

            verificationCodeRepository.delete(entry);
            log.info("OTP verification successful for userEmail={}. OTP record deleted.", mask(email));

            return true;

        } catch (Exception ex) {
            log.error("Unexpected error during OTP verification for userEmail={}. error={}", mask(email), ex.getMessage(), ex);
            throw ex;
        }
    }

    // Helper — masks user email
    private String mask(String email) {
        if (email == null || !email.contains("@")) return "****";
        String[] parts = email.split("@");
        return parts[0].charAt(0) + "***@" + parts[1];
    }
}
