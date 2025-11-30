package com.kudiapp.kudiapp.services;

import org.springframework.stereotype.Component;

@Component
public interface EmailService {
    void sendOtp(String to, String subject, String text);
////    void sendEmail(String to, String subject, String text);
//    GenericResponse verifyEmail(String encodedToken);
//
//    void sendVerificationEmail(String email, String name, String templateName, String token);
//
//    void sendPasswordResetEmail(String email, String name, String token);
//
//    void sendOtp(String email, String yourVerificationCode, String content);
}
