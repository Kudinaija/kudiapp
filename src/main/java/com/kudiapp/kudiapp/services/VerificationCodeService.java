package com.kudiapp.kudiapp.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public interface VerificationCodeService {


    void sendOtpCode(String email);

    @Transactional
    void sendOtpForRegistration(String email);

    boolean verifyOtpCode(String email, String code);
}
