package com.kudiapp.kudiapp.dto.productService;

import com.kudiapp.kudiapp.enums.productService.CartStatus;
import com.kudiapp.kudiapp.enums.productService.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDto {
    
    private Long id;
    private String cartReference;
    private Long userId;
    private CartStatus status;
    
    private Integer itemCount;
    private List<OrderSummaryDto> orderSummaries;
    
    private BigDecimal subtotal;
    private BigDecimal totalServiceFee;
    private BigDecimal totalAmount;
    private Currency currency;
    
    private Map<String, BigDecimal> liveExchangeRates;
    
    private LocalDateTime lastActivityAt;
    private LocalDateTime checkedOutAt;
    private String paymentReference;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderSummaryDto {
        private Long orderId;
        private String orderReference;
        private String serviceProductName;
        private String servicePlanName;
        private BigDecimal amount;
        private BigDecimal serviceFee;
        private BigDecimal totalAmount;
        private Currency currency;
    }
}