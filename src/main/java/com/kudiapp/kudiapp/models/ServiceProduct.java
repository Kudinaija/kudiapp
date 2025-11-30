package com.kudiapp.kudiapp.models;

import com.kudiapp.kudiapp.enums.Category;
import com.kudiapp.kudiapp.enums.PRODUCT_TITLE;
import com.kudiapp.kudiapp.enums.UrgencyType;
import com.kudiapp.kudiapp.models.baseclass.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(
        name = "service_products",
        indexes = {
                @Index(name = "idx_product_category", columnList = "category"),
                @Index(name = "idx_product_title", columnList = "productTitle"),
                @Index(name = "idx_product_urgent_type", columnList = "urgentTypes")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProduct extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    private String categoryItems;

    @Enumerated(EnumType.STRING)
    private PRODUCT_TITLE productTitle;

    @Column(columnDefinition = "TEXT")
    private String productDescription;

    @Enumerated(EnumType.STRING)
    private UrgencyType urgentTypes;

//    /** Dynamic metadata stored as JSONB */
//    @Type(org.hibernate.type.JsonType.class)
//    @Column(columnDefinition = "jsonb")
//    private Map<String, Object> metadata;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

}
