package com.kudiapp.kudiapp.models.productService;

import com.kudiapp.kudiapp.enums.productService.Currency;
import com.kudiapp.kudiapp.enums.productService.ServiceProductPlanStatus;
import com.kudiapp.kudiapp.enums.productService.ServiceProductPlanType;
import com.kudiapp.kudiapp.models.baseclass.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Builder;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(
        name = "service_product_plans",
        indexes = {
                @Index(name = "idx_plan_service_product", columnList = "service_product_id"),
                @Index(name = "idx_plan_name", columnList = "plan_name"),
                @Index(name = "idx_plan_status", columnList = "status"),
                @Index(name = "idx_plan_type", columnList = "plan_type"),
                @Index(name = "idx_plan_active_status", columnList = "service_product_id, status")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_plan_name_service_product",
                        columnNames = {"plan_name", "service_product_id"}
                )
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"serviceProduct", "productPrice"})
@EqualsAndHashCode(callSuper = true, exclude = {"serviceProduct", "productPrice"})
public class ServiceProductPlan extends BaseEntity {

    @Column(name = "plan_name", nullable = false, length = 100)
    private String planName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "service_product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_plan_service_product")
    )
    private ServiceProduct serviceProduct;

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 10)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private ServiceProductPlanStatus status = ServiceProductPlanStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false, length = 30)
    @Builder.Default
    private ServiceProductPlanType planType = ServiceProductPlanType.DEFAULT;

    @Column(name = "plan_description", columnDefinition = "TEXT")
    private String planDescription;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @OneToOne(
            mappedBy = "servicePlan",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private ServiceProductPrice productPrice;

    // Helper methods for bidirectional relationship
    public void setProductPrice(ServiceProductPrice price) {
        if (price == null) {
            if (this.productPrice != null) {
                this.productPrice.setServicePlan(null);
            }
        } else {
            price.setServicePlan(this);
        }
        this.productPrice = price;
    }

    public boolean isActive() {
        return ServiceProductPlanStatus.ACTIVE.equals(this.status);
    }
}
