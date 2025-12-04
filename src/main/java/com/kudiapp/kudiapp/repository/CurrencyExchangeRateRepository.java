package com.kudiapp.kudiapp.repository;

import com.kudiapp.kudiapp.enums.productService.Currency;
import com.kudiapp.kudiapp.models.productService.CurrencyExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyExchangeRateRepository extends JpaRepository<CurrencyExchangeRate, Long> {

    @Query("SELECT cer FROM CurrencyExchangeRate cer " +
           "WHERE cer.fromCurrency = :from " +
           "AND cer.toCurrency = :to " +
           "AND cer.isActive = true " +
           "AND cer.effectiveDate <= :currentDate " +
           "AND (cer.expiryDate IS NULL OR cer.expiryDate >= :currentDate) " +
           "ORDER BY cer.effectiveDate DESC")
    Optional<CurrencyExchangeRate> findLatestEffectiveRate(
            @Param("from") Currency from,
            @Param("to") Currency to,
            @Param("currentDate") LocalDateTime currentDate
    );

    List<CurrencyExchangeRate> findByFromCurrencyAndToCurrencyAndIsActiveTrue(
            Currency fromCurrency,
            Currency toCurrency
    );

    @Query("SELECT cer FROM CurrencyExchangeRate cer " +
           "WHERE cer.isActive = true " +
           "AND cer.effectiveDate <= CURRENT_TIMESTAMP " +
           "AND (cer.expiryDate IS NULL OR cer.expiryDate >= CURRENT_TIMESTAMP)")
    List<CurrencyExchangeRate> findAllCurrentlyEffectiveRates();
}