package com.kudiapp.kudiapp.dto.productService;

import com.kudiapp.kudiapp.enums.productService.Currency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProductPriceRequest {

    @NotNull(message = "Service plan ID is required")
    private Long servicePlanId;

    @NotNull(message = "Default price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Default price must be greater than zero")
    @Digits(integer = 15, fraction = 4)
    private BigDecimal defaultPrice=BigDecimal.ZERO;

    @DecimalMin(value = "0.0")
    @Digits(integer = 15, fraction = 4)
    private BigDecimal amount=BigDecimal.ZERO;

    @NotNull(message = "Default currency is required")
    private Currency defaultCurrency=Currency.USD;

    @NotNull(message = "Amount currency is required")
    private Currency amountCurrency=Currency.NGN;
}