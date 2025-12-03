package com.kudiapp.kudiapp.dto.productService;

import com.kudiapp.kudiapp.enums.productService.Currency;
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
public class ServiceProductPriceResponse {

    private Long id;
    private Long servicePlanId;
    private String servicePlanName;
    private BigDecimal defaultPrice;
    private Currency defaultCurrency;
    private BigDecimal amountToPay;
    private Currency amountCurrency;
    private BigDecimal conversionRate;
    private Long rateTimestamp;
    private String rateSource;
    private Boolean isRateStale;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
