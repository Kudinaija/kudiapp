package com.kudiapp.kudiapp.dto.productService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for submitting a new service product request.
 * This represents a user's intention to order a specific service product plan.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    @NotNull(message = "Service product ID is required")
    private Long serviceProductId;

    @NotNull(message = "Service plan ID is required")
    private Long servicePlanId;

    @Size(max = 150, message = "Credential username/email must not exceed 150 characters")
    private String credentialUsernameOrEmail;

    @Size(max = 255, message = "Credential password must not exceed 255 characters")
    private String credentialPassword;

    /**
     * Dynamic metadata for service-specific information.
     * Examples: 
     * - Journal submission: {"manuscriptTitle": "AI in Healthcare", "journalName": "Nature"}
     * - Exam payment: {"examType": "IELTS", "testCenter": "Lagos", "preferredDate": "2025-03-15"}
     * - Hotel booking: {"checkIn": "2025-02-10", "checkOut": "2025-02-15", "guests": 2}
     */
    private Map<String, Object> metadata;
}