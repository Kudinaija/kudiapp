package com.kudiapp.kudiapp.models.productService;

import com.kudiapp.kudiapp.enums.productService.Currency;
import com.kudiapp.kudiapp.models.baseclass.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "currency_exchange_rates",
        indexes = {
                @Index(name = "idx_rate_from_to", columnList = "from_currency, to_currency"),
                @Index(name = "idx_rate_effective_date", columnList = "effective_date"),
                @Index(name = "idx_rate_active", columnList = "is_active, effective_date")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_from_to_currency_date",
                        columnNames = {"from_currency", "to_currency", "effective_date"}
                )
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CurrencyExchangeRate extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "from_currency", nullable = false, length = 10)
    private Currency fromCurrency;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_currency", nullable = false, length = 10)
    private Currency toCurrency;

    @Column(name = "exchange_rate", nullable = false, precision = 19, scale = 6)
    private BigDecimal exchangeRate;

    @Column(name = "effective_date", nullable = false)
    private LocalDateTime effectiveDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "rate_source", length = 100)
    private String rateSource;

    @Column(name = "rate_provider", length = 100)
    private String rateProvider;

    public boolean isEffective() {
        LocalDateTime now = LocalDateTime.now();
        boolean afterEffective = effectiveDate == null || !now.isBefore(effectiveDate);
        boolean beforeExpiry = expiryDate == null || !now.isAfter(expiryDate);
        return isActive && afterEffective && beforeExpiry;
    }
}
