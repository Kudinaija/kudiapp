package com.kudiapp.kudiapp.controller;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.request.authDTOS.*;
import com.kudiapp.kudiapp.dto.response.CompleteResetRequest;
import com.kudiapp.kudiapp.dto.token.RefreshTokenRequest;
import com.kudiapp.kudiapp.services.AuthService;
import com.kudiapp.kudiapp.services.PasswordResetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and account management")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/contact-us")
    public ResponseEntity<GenericResponse> contactUs(@RequestBody ContactUsRequest contactUsRequest){
    GenericResponse genericResponse = authService.contactUs(contactUsRequest);
    return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

    @PutMapping("/contact-us/attendedTo/{contactusId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<GenericResponse> contactUsAttendedTo(@PathVariable Long contactusId){
    GenericResponse genericResponse = authService.contactUsAttendedTo(contactusId);
    return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
    }

    @GetMapping("/all-contact-us")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<GenericResponse> getAllContactUs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GenericResponse response = authService.getAllContactUs(page, size);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

        @PostMapping("/register-user")
        public ResponseEntity<GenericResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
            GenericResponse response = authService.register(registerRequest);
            return new ResponseEntity<>(response, response.getHttpStatus());
        }

        @GetMapping("/verify-email")
        public ResponseEntity<GenericResponse> verifyEmail(@RequestParam String email, @RequestParam String code){
            GenericResponse genericResponse = authService.verifyEmail(email, code);
            return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
        }

    @GetMapping("/resend-otp")
    public ResponseEntity<GenericResponse> resendOtp(@RequestParam String email) {
        GenericResponse response = authService.resendOtp(email);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/login")
        public ResponseEntity<GenericResponse> login(@RequestBody LoginRequest loginRequest) {
            GenericResponse response =  authService.login(loginRequest);
            return new ResponseEntity<>(response, response.getHttpStatus());
        }

        @PostMapping("/login2fa")
        public ResponseEntity<GenericResponse> login2fa(@RequestParam String email, @RequestParam String code) {
            GenericResponse response =  authService.login2fa(email, code);
            return new ResponseEntity<>(response, response.getHttpStatus());
        }

        @PostMapping("/refresh")
        public ResponseEntity<GenericResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
            GenericResponse response = authService.refreshToken(request);
            return new ResponseEntity<>(response, response.getHttpStatus());
        }

        @PostMapping("/change-password")
        public ResponseEntity<GenericResponse> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
            GenericResponse genericResponse = authService.changePassword(request);
            return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
        }

        @DeleteMapping("/delete/{userId}")
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        public ResponseEntity<GenericResponse> deleteUser(@PathVariable Long userId){
            GenericResponse genericResponse = authService.deleteAccount(userId);
            return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
        }

        @PostMapping("/forgot-password")
        public ResponseEntity<GenericResponse> forgotPassword(@Valid @RequestBody InitiatePasswordReset request) {
            GenericResponse genericResponse = passwordResetService.initiatePasswordReset(request);
            return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
        }

        @GetMapping("/validate-reset-token")
        public ResponseEntity<GenericResponse> validateResetToken(@RequestParam String token) {
            GenericResponse genericResponse = passwordResetService.validateResetToken(token);
            return new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
        }

        @PostMapping("/reset-password")
        public ResponseEntity<GenericResponse> resetPassword(@Valid @RequestBody CompleteResetRequest request) {
            GenericResponse genericResponse = passwordResetService.completePasswordReset(request);
            return  new ResponseEntity<>(genericResponse, genericResponse.getHttpStatus());
        }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.ok("Logged out successfully");
    }
}
