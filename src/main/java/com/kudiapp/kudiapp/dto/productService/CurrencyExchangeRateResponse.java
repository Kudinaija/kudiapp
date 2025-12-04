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
public class CurrencyExchangeRateResponse {

    private Long id;
    private Currency fromCurrency;
    private Currency toCurrency;
    private BigDecimal exchangeRate;
    private LocalDateTime effectiveDate;
    private LocalDateTime expiryDate;
    private Boolean isActive;
    private String rateSource;
    private String rateProvider;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}