package com.kudiapp.kudiapp.models.productService;

import com.kudiapp.kudiapp.enums.productService.CartStatus;
import com.kudiapp.kudiapp.enums.productService.Currency;
import com.kudiapp.kudiapp.models.baseclass.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "carts",
        indexes = {
                @Index(name = "idx_cart_user_id", columnList = "user_id"),
                @Index(name = "idx_cart_status", columnList = "status"),
                @Index(name = "idx_cart_reference", columnList = "cart_reference"),
                @Index(name = "idx_cart_created_at", columnList = "created_at")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_active_cart", columnNames = {"user_id", "status"})
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"orders"})
@EqualsAndHashCode(callSuper = true, exclude = {"orders"})
public class Cart extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "cart_reference", nullable = false, unique = true, length = 50)
    private String cartReference;

    @Column(name = "stripe_session_id")
    private String stripeSessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private CartStatus status = CartStatus.ACTIVE;

    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    @Column(name = "item_count", nullable = false)
    @Builder.Default
    private Integer itemCount = 0;

    @Column(name = "subtotal", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "total_service_fee", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal totalServiceFee = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 10)
    @Builder.Default
    private Currency currency = Currency.NGN;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @Column(name = "checked_out_at")
    private LocalDateTime checkedOutAt;

    @Column(name = "payment_reference", length = 100)
    private String paymentReference;

    public void addOrder(Order order) {
        orders.add(order);
        order.setCart(this);
        updateCartTotals();
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        order.setCart(null);
        updateCartTotals();
    }

    public void clearOrders() {
        orders.forEach(order -> order.setCart(null));
        orders.clear();
        updateCartTotals();
    }

    public void updateCartTotals() {
        this.itemCount = orders.size();
        
        this.subtotal = orders.stream()
                .map(Order::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.totalServiceFee = orders.stream()
                .map(Order::getServiceFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.totalAmount = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.lastActivityAt = LocalDateTime.now();
    }

    public void checkout(String paymentRef) {
        this.status = CartStatus.CHECKOUT_INITIATED;
        this.checkedOutAt = LocalDateTime.now();
        this.paymentReference = paymentRef;
        orders.forEach(order -> order.setIsInCart(false));
    }

    public boolean isEmpty() {
        return orders == null || orders.isEmpty();
    }

    public boolean canBeModified() {
        return CartStatus.ACTIVE.equals(this.status);
    }

    public boolean isExpired() {
        return this.lastActivityAt != null && 
               this.lastActivityAt.isBefore(LocalDateTime.now().minusDays(7));
    }
}