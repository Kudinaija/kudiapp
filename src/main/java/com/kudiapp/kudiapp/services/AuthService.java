package com.kudiapp.kudiapp.services;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.request.authDTOS.ChangePasswordRequest;
import com.kudiapp.kudiapp.dto.request.authDTOS.ContactUsRequest;
import com.kudiapp.kudiapp.dto.request.authDTOS.LoginRequest;
import com.kudiapp.kudiapp.dto.request.authDTOS.RegisterRequest;
import com.kudiapp.kudiapp.dto.token.RefreshTokenRequest;
import org.springframework.stereotype.Component;

@Component
public interface AuthService {

    GenericResponse login(LoginRequest loginRequest);
    GenericResponse register(RegisterRequest registerRequest);
    GenericResponse contactUs(ContactUsRequest contactUsRequest);
//    GenericResponse verifyEmail(String token);
    GenericResponse changePassword(ChangePasswordRequest request);
    GenericResponse deleteAccount(Long userId);

    GenericResponse getAllContactUs(int page, int size);

    GenericResponse refreshToken(RefreshTokenRequest request);

    GenericResponse contactUsAttendedTo(Long contactusId);

    GenericResponse verifyEmail(String email, String code);

    GenericResponse resendOtp(String email);

    GenericResponse login2fa(String email, String code);
}