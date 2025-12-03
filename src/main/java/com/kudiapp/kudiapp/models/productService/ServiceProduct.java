package com.kudiapp.kudiapp.models.productService;

import com.kudiapp.kudiapp.enums.productService.Category;
import com.kudiapp.kudiapp.enums.productService.PRODUCT_TITLE;
import com.kudiapp.kudiapp.enums.productService.ServiceProductStatus;
import com.kudiapp.kudiapp.enums.productService.UrgencyType;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(
        name = "service_products",
        indexes = {
                @Index(name = "idx_service_product_category", columnList = "category"),
                @Index(name = "idx_service_product_title", columnList = "product_title"),
                @Index(name = "idx_service_product_urgency", columnList = "urgency_type"),
                @Index(name = "idx_service_product_status", columnList = "status"),
                @Index(name = "idx_service_product_created_at", columnList = "created_at"),
                @Index(name = "idx_service_product_public", columnList = "status, category")
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, exclude = {"productPlans"})
@EqualsAndHashCode(callSuper = true, exclude = {"productPlans"})
public class ServiceProduct extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Category category;

    @Column(name = "category_items", columnDefinition = "TEXT")
    private String categoryItems;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_title", nullable = false, length = 100)
    private PRODUCT_TITLE productTitle;

    @Column(name = "product_description", columnDefinition = "TEXT")
    private String productDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency_type", length = 50)
    private UrgencyType urgencyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private ServiceProductStatus status = ServiceProductStatus.DRAFT;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @OneToMany(
            mappedBy = "serviceProduct",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<ServiceProductPlan> productPlans = new ArrayList<>();

    public void addProductPlan(ServiceProductPlan plan) {
        productPlans.add(plan);
        plan.setServiceProduct(this);
    }

    public void removeProductPlan(ServiceProductPlan plan) {
        productPlans.remove(plan);
        plan.setServiceProduct(null);
    }

    public boolean isPubliclyVisible() {
        return status != null && status.isPubliclyVisible();
    }

    public boolean canBeOrdered() {
        return status != null && status.canBeOrdered();
    }

    public boolean isDraft() {
        return ServiceProductStatus.DRAFT.equals(this.status);
    }

    public boolean isActive() {
        return ServiceProductStatus.ACTIVE.equals(this.status);
    }
}