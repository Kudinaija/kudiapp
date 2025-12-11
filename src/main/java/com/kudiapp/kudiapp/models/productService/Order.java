package com.kudiapp.kudiapp.models.productService;

import com.kudiapp.kudiapp.enums.productService.Currency;
import com.kudiapp.kudiapp.enums.productService.OrderAction;
import com.kudiapp.kudiapp.enums.productService.OrderStatus;
import com.kudiapp.kudiapp.models.baseclass.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.Map;

@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_order_user_id", columnList = "user_id"),
                @Index(name = "idx_order_status", columnList = "status"),
                @Index(name = "idx_order_action", columnList = "action"),
                @Index(name = "idx_order_reference", columnList = "order_reference"),
                @Index(name = "idx_order_cart_id", columnList = "cart_id"),
                @Index(name = "idx_order_service_product", columnList = "service_product_id"),
                @Index(name = "idx_order_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"cart"})
@EqualsAndHashCode(callSuper = true, exclude = {"cart"})
public class Order extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "order_reference", nullable = false, unique = true, length = 50)
    private String orderReference;

    @Column(name = "service_product_id", nullable = false)
    private Long serviceProductId;

    @Column(name = "service_product_name", nullable = false, length = 200)
    private String serviceProductName;

    @Column(name = "service_plan_id", nullable = false)
    private Long servicePlanId;

    @Column(name = "service_plan_name", nullable = false, length = 150)
    private String servicePlanName;

    @Column(name = "credential_username_or_email", length = 255)
    private String credentialUsernameOrEmail;

    @Column(name = "credential_password", length = 500)
    private String credentialPassword;

    @Column(name = "default_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal defaultAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_currency", nullable = false, length = 10)
    private Currency defaultCurrency;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "amount_currency", nullable = false, length = 10)
    private Currency amountCurrency;

    @Column(name = "currency_conversion_rate", nullable = false, precision = 19, scale = 6)
    private BigDecimal currencyConversionRate;

    @Column(name = "service_fee", precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal serviceFee = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 30)
    @Builder.Default
    private OrderAction action = OrderAction.PENDING_REVIEW;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", foreignKey = @ForeignKey(name = "fk_order_cart"))
    private Cart cart;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @Column(name = "payment_reference", length = 100)
    private String paymentReference;

    @Column(name = "is_in_cart", nullable = false)
    @Builder.Default
    private Boolean isInCart = true;

    public void calculateTotalAmount() {
        if (amount != null && serviceFee != null) {
            this.totalAmount = amount.add(serviceFee);
        } else if (amount != null) {
            this.totalAmount = amount;
        }
    }

    public boolean canBeModified() {
        return OrderStatus.PENDING.equals(this.status) && Boolean.TRUE.equals(this.isInCart);
    }

    public boolean requiresAdminAttention() {
        return OrderAction.PENDING_REVIEW.equals(this.action) || 
               OrderAction.IN_PROGRESS.equals(this.action);
    }
}