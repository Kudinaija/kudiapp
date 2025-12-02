package com.kudiapp.kudiapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDto {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private boolean enabled;
    private boolean verified;
    private String profilePicture;
    private String userUUID;
    private String username;
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private List<String> roles;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}

