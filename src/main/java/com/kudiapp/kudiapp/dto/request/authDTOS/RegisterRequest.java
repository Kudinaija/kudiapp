package com.kudiapp.kudiapp.dto.request.authDTOS;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {


    @NotBlank(message = "Firstname is required!")
    private String firstname;

    @NotBlank(message = "Lastname is required!")
    private String lastname;

    @Email(message = "Email is not in valid format!")
    @NotBlank(message = "Email is required!")
    private String email;

    @NotBlank(message = "Password is required!")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters!")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.])[A-Za-z\\d@$!%*?&.]{8,20}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character!"
    )
    private String password;

    private String phoneNumber;

}