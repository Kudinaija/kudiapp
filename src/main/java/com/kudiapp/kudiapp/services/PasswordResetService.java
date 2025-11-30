package com.kudiapp.kudiapp.services;



import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.request.authDTOS.InitiatePasswordReset;
import com.kudiapp.kudiapp.dto.response.CompleteResetRequest;
import org.springframework.stereotype.Component;

@Component
public interface PasswordResetService {
    GenericResponse initiatePasswordReset(InitiatePasswordReset resetPasswordRequest);

    GenericResponse validateResetToken(String token);

    GenericResponse completePasswordReset(CompleteResetRequest request);
}
