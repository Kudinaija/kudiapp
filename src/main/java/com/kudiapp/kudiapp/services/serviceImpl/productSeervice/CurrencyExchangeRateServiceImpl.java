package com.kudiapp.kudiapp.services.serviceImpl.productSeervice;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.productService.CurrencyExchangeRateRequest;
import com.kudiapp.kudiapp.dto.productService.CurrencyExchangeRateResponse;
import com.kudiapp.kudiapp.enums.productService.Currency;
import com.kudiapp.kudiapp.exceptions.InvalidOperationException;
import com.kudiapp.kudiapp.exceptions.ResourceNotFoundException;
import com.kudiapp.kudiapp.models.productService.CurrencyExchangeRate;
import com.kudiapp.kudiapp.repository.CurrencyExchangeRateRepository;
import com.kudiapp.kudiapp.services.productService.CurrencyExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CurrencyExchangeRateServiceImpl implements CurrencyExchangeRateService {

    private final CurrencyExchangeRateRepository exchangeRateRepository;

    @Override
    public GenericResponse createExchangeRate(CurrencyExchangeRateRequest request) {
        log.info("Creating exchange rate from {} to {}", 
                request.getFromCurrency(), request.getToCurrency());

        validateCurrencyPair(request.getFromCurrency(), request.getToCurrency());
        validateDates(request.getEffectiveDate(), request.getExpiryDate());

        CurrencyExchangeRate rate = buildExchangeRateFromRequest(new CurrencyExchangeRate(), request);
        CurrencyExchangeRate savedRate = exchangeRateRepository.save(rate);

        log.info("Successfully created exchange rate with ID: {}", savedRate.getId());

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Exchange rate created successfully")
                .httpStatus(HttpStatus.CREATED)
                .data(mapToResponse(savedRate))
                .build();
    }

    @Override
    public GenericResponse updateExchangeRate(Long id, CurrencyExchangeRateRequest request) {
        log.info("Updating exchange rate with ID: {}", id);

        CurrencyExchangeRate rate = findExchangeRateById(id);
        validateCurrencyPair(request.getFromCurrency(), request.getToCurrency());
        validateDates(request.getEffectiveDate(), request.getExpiryDate());

        buildExchangeRateFromRequest(rate, request);
        CurrencyExchangeRate updatedRate = exchangeRateRepository.save(rate);

        log.info("Successfully updated exchange rate with ID: {}", id);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Exchange rate updated successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponse(updatedRate))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GenericResponse getExchangeRateById(Long id) {
        log.info("Retrieving exchange rate with ID: {}", id);

        CurrencyExchangeRate rate = findExchangeRateById(id);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Exchange rate retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponse(rate))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GenericResponse getLatestEffectiveRate(Currency fromCurrency, Currency toCurrency) {
        log.info("Retrieving latest effective rate from {} to {}", fromCurrency, toCurrency);

        validateCurrencyPair(fromCurrency, toCurrency);

        CurrencyExchangeRate rate = exchangeRateRepository
                .findLatestEffectiveRate(fromCurrency, toCurrency, LocalDateTime.now())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No effective exchange rate found from " + fromCurrency + " to " + toCurrency));

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Latest effective rate retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponse(rate))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GenericResponse getAllCurrentlyEffectiveRates() {
        log.info("Retrieving all currently effective exchange rates");

        List<CurrencyExchangeRate> rates = exchangeRateRepository.findAllCurrentlyEffectiveRates();
        List<CurrencyExchangeRateResponse> responses = rates.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Currently effective rates retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(responses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getConversionRate(Currency fromCurrency, Currency toCurrency) {
        log.debug("Getting conversion rate from {} to {}", fromCurrency, toCurrency);

        if (fromCurrency.equals(toCurrency)) {
            return BigDecimal.ONE;
        }

        CurrencyExchangeRate rate = exchangeRateRepository
                .findLatestEffectiveRate(fromCurrency, toCurrency, LocalDateTime.now())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No conversion rate available from " + fromCurrency + " to " + toCurrency));

        return rate.getExchangeRate();
    }

    @Override
    public GenericResponse deleteExchangeRate(Long id) {
        log.info("Deleting exchange rate with ID: {}", id);

        CurrencyExchangeRate rate = findExchangeRateById(id);
        exchangeRateRepository.delete(rate);

        log.info("Successfully deleted exchange rate with ID: {}", id);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Exchange rate deleted successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    private CurrencyExchangeRate findExchangeRateById(Long id) {
        return exchangeRateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Exchange rate not found with ID: " + id));
    }

    @SneakyThrows
    private void validateCurrencyPair(Currency fromCurrency, Currency toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            throw new InvalidOperationException(
                    "From currency and to currency cannot be the same");
        }
    }

    @SneakyThrows
    private void validateDates(LocalDateTime effectiveDate, LocalDateTime expiryDate) {
        if (expiryDate != null && effectiveDate.isAfter(expiryDate)) {
            throw new InvalidOperationException(
                    "Effective date cannot be after expiry date");
        }
    }

    private CurrencyExchangeRate buildExchangeRateFromRequest(
            CurrencyExchangeRate rate,
            CurrencyExchangeRateRequest request) {
        
        rate.setFromCurrency(request.getFromCurrency());
        rate.setToCurrency(request.getToCurrency());
        rate.setExchangeRate(request.getExchangeRate());
        rate.setEffectiveDate(request.getEffectiveDate());
        rate.setExpiryDate(request.getExpiryDate());
        rate.setRateSource(request.getRateSource());
        rate.setRateProvider(request.getRateProvider());
        
        if (request.getIsActive() != null) {
            rate.setIsActive(request.getIsActive());
        } else if (rate.getIsActive() == null) {
            rate.setIsActive(true);
        }
        
        return rate;
    }

    private CurrencyExchangeRateResponse mapToResponse(CurrencyExchangeRate rate) {
        return CurrencyExchangeRateResponse.builder()
                .id(rate.getId())
                .fromCurrency(rate.getFromCurrency())
                .toCurrency(rate.getToCurrency())
                .exchangeRate(rate.getExchangeRate())
                .effectiveDate(rate.getEffectiveDate())
                .expiryDate(rate.getExpiryDate())
                .isActive(rate.getIsActive())
                .rateSource(rate.getRateSource())
                .rateProvider(rate.getRateProvider())
                .createdAt(rate.getCreatedAt())
                .updatedAt(rate.getUpdatedAt())
                .build();
    }
}