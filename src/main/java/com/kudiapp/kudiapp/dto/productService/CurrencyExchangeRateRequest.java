package com.kudiapp.kudiapp.dto.productService;

import com.kudiapp.kudiapp.enums.productService.Currency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class CurrencyExchangeRateRequest {

    @NotNull(message = "From currency is required")
    private Currency fromCurrency;

    @NotNull(message = "To currency is required")
    private Currency toCurrency;

    @NotNull(message = "Exchange rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Exchange rate must be greater than zero")
    @Digits(integer = 13, fraction = 6)
    private BigDecimal exchangeRate;

    @NotNull(message = "Effective date is required")
    private LocalDateTime effectiveDate;

    private LocalDateTime expiryDate;

    @Size(max = 100, message = "Rate source must not exceed 100 characters")
    private String rateSource;

    @Size(max = 100, message = "Rate provider must not exceed 100 characters")
    private String rateProvider;

    private Boolean isActive;
}