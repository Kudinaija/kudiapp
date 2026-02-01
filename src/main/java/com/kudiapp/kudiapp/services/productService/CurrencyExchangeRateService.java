package com.kudiapp.kudiapp.services.productService;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.productService.CurrencyExchangeRateRequest;
import com.kudiapp.kudiapp.enums.productService.Currency;


import java.math.BigDecimal;

public interface CurrencyExchangeRateService {

    GenericResponse createExchangeRate(CurrencyExchangeRateRequest request);

    GenericResponse updateExchangeRate(Long id, CurrencyExchangeRateRequest request);

    GenericResponse getExchangeRateById(Long id);

    GenericResponse getLatestEffectiveRate(Currency fromCurrency, Currency toCurrency);

    GenericResponse getAllCurrentlyEffectiveRates();

    BigDecimal getConversionRate(Currency fromCurrency, Currency toCurrency);

    GenericResponse deleteExchangeRate(Long id);
}