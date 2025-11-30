package com.kudiapp.kudiapp.services;

import org.springframework.stereotype.Component;

@Component
public interface VerificationCodeService {

    void sendOtpCode(String email);
    boolean verifyOtpCode(String email, String code);
}
