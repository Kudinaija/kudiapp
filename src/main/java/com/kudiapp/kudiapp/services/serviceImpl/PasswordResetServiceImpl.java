package com.kudiapp.kudiapp.services.serviceImpl;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.request.authDTOS.InitiatePasswordReset;
import com.kudiapp.kudiapp.dto.response.CompleteResetRequest;
import com.kudiapp.kudiapp.exceptions.InvalidRequestException;
import com.kudiapp.kudiapp.exceptions.InvalidTokenException;
import com.kudiapp.kudiapp.exceptions.UserNotFoundException;
import com.kudiapp.kudiapp.models.PasswordResetToken;
import com.kudiapp.kudiapp.models.User;
import com.kudiapp.kudiapp.repository.PasswordResetTokenRepository;
import com.kudiapp.kudiapp.repository.UserRepository;
import com.kudiapp.kudiapp.services.EmailService;
import com.kudiapp.kudiapp.services.PasswordResetService;
import com.kudiapp.kudiapp.utills.OtpUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
@Transactional
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {
    
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetServiceImpl(UserRepository userRepository, PasswordResetTokenRepository tokenRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public GenericResponse initiatePasswordReset(InitiatePasswordReset resetPasswordRequest) {
        User user = userRepository.findByEmailIgnoreCase(resetPasswordRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User with this email does not exist"));

        // Invalidate previous tokens for this user
        tokenRepository.invalidateExistingTokens(user.getId());

        // Generate a fresh token
        String token = OtpUtil.generateSixDigitCode();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token) // store raw token in DB
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .expiryDate(Instant.now().plus(15, ChronoUnit.MINUTES))
                .used(false)
                .build();

        tokenRepository.save(resetToken);

        // Encode only for email link
        emailService.sendOtp(
                user.getEmail(),
                "Reset Password",
                token
        );

        return GenericResponse.builder()
                .isSuccess(true)
                .httpStatus(HttpStatus.OK)
                .message("If an account exists with this email, a reset link has been sent")
                .build();
    }

    @Override
    public GenericResponse validateResetToken(String encodedToken) {
        String token = decodeToken(encodedToken); // decode before lookup

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new InvalidTokenException("Token has expired");
        }

        if (resetToken.isUsed()) {
            throw new InvalidTokenException("Token has already been used");
        }

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Token is valid")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Override
    @Transactional
    public GenericResponse completePasswordReset(CompleteResetRequest request) {

        String token = request.getToken();

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new InvalidTokenException("Token has expired");
        }

        if (resetToken.isUsed()) {
            throw new InvalidTokenException("Token has already been used");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidRequestException("Passwords do not match");
        }

        // Update password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordChangedDate(Instant.now());
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Password has been reset successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }
    
    @Scheduled(cron = "0 0 3 * * ?") // Runs daily at 3 AM
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens();
    }

//    private String encodeToken(String token) {
//        return Base64.getUrlEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
//    }

    private String decodeToken(String encodedToken) {
        return new String(Base64.getUrlDecoder().decode(encodedToken), StandardCharsets.UTF_8);
    }
}