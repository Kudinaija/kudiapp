package com.kudiapp.kudiapp.dto.productService;

import com.kudiapp.kudiapp.enums.productService.Currency;
import com.kudiapp.kudiapp.enums.productService.OrderAction;
import com.kudiapp.kudiapp.enums.productService.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    
    private Long id;
    private String orderReference;
    private Long userId;
    private String userName;
    private String email;
    private String phoneNumber;
    
    // Service product details
    private Long serviceProductId;
    private String serviceProductName;
    private Long servicePlanId;
    private String servicePlanName;
    
    // Credentials (only visible to admin)
    private String credentialUsernameOrEmail;
    
    // Pricing details
    private BigDecimal defaultAmount;
    private Currency defaultCurrency;
    private BigDecimal amount;
    private Currency amountCurrency;
    private BigDecimal currencyConversionRate;
    private BigDecimal serviceFee;
    private BigDecimal totalAmount;
    
    // Status tracking
    private OrderStatus status;
    private OrderAction action;
    
    // Additional information
    private Map<String, Object> metadata;
    private String adminNotes;
    private String paymentReference;
    private Boolean isInCart;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}