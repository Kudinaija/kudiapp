package com.kudiapp.kudiapp.models.productService;

import com.kudiapp.kudiapp.enums.productService.Currency;
import com.kudiapp.kudiapp.models.baseclass.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(
        name = "service_product_prices",
        indexes = {
                @Index(name = "idx_price_service_plan", columnList = "service_plan_id"),
                @Index(name = "idx_price_currencies", columnList = "default_currency, amount_currency")
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"servicePlan"})
@EqualsAndHashCode(callSuper = true, exclude = {"servicePlan"})
public class ServiceProductPrice extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "service_plan_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_price_service_plan")
    )
    private ServiceProductPlan servicePlan;

    @Column(name = "default_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal defaultPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_currency", nullable = false, length = 10)
    private Currency defaultCurrency;

    @Column(name = "amount_to_pay", nullable = false, precision = 19, scale = 4)
    private BigDecimal amountToPay;

    @Enumerated(EnumType.STRING)
    @Column(name = "amount_currency", nullable = false, length = 10)
    private Currency amountCurrency;

    @Column(name = "conversion_rate", nullable = false, precision = 19, scale = 6)
    private BigDecimal conversionRate;

    @Column(name = "rate_timestamp")
    private Long rateTimestamp;

    @Column(name = "rate_source", length = 50)
    private String rateSource;

    /**
     * Calculates the amount to pay based on default price and conversion rate.
     * Uses HALF_UP rounding mode for financial calculations.
     */
    public void calculateAmountToPay() {
        if (defaultPrice != null && conversionRate != null) {
            this.amountToPay = defaultPrice
                    .multiply(conversionRate)
                    .setScale(4, RoundingMode.HALF_UP);
        }
    }

    /**
     * Check if the conversion rate needs updating based on timestamp.
     * Considers rate stale if older than 24 hours.
     */
    public boolean isRateStale() {
        if (rateTimestamp == null) {
            return true;
        }
        long currentTime = System.currentTimeMillis();
        long twentyFourHoursInMillis = 24 * 60 * 60 * 1000L;
        return (currentTime - rateTimestamp) > twentyFourHoursInMillis;
    }
}