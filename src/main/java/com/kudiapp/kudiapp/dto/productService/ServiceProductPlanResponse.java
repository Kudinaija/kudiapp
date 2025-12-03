package com.kudiapp.kudiapp.dto.productService;

import com.kudiapp.kudiapp.enums.productService.Currency;
import com.kudiapp.kudiapp.enums.productService.ServiceProductPlanStatus;
import com.kudiapp.kudiapp.enums.productService.ServiceProductPlanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProductPlanResponse {

    private Long id;
    private String planName;
    private Long serviceProductId;
    private String serviceProductTitle;
    private BigDecimal amount;
    private Currency currency;
    private ServiceProductPlanStatus status;
    private ServiceProductPlanType planType;
    private String planDescription;
    private Integer displayOrder;
    private Boolean isFeatured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}