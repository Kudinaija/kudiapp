package com.kudiapp.kudiapp.dto.request.authDTOS;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required!")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters!")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.])[A-Za-z\\d@$!%*?&.]{8,20}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character!"
    )
    private String currentPassword;

    @NotBlank(message = "New password is required!")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters!")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.])[A-Za-z\\d@$!%*?&.]{8,20}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character!"
    )
    private String newPassword;
}