package com.kudiapp.kudiapp.dto.response.specifications;

import com.kudiapp.kudiapp.enums.Category;
import com.kudiapp.kudiapp.enums.UrgencyType;
import com.kudiapp.kudiapp.models.ServiceProduct;
import org.springframework.data.jpa.domain.Specification;

public class ServiceProductSpecifications {

    public static Specification<ServiceProduct> hasCategory(String category) {
        return (root, query, cb) -> {
            if (category == null || category.isBlank()) return null;
            return cb.equal(root.get("category"), Category.valueOf(category));
        };
    }

    public static Specification<ServiceProduct> hasTitle(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) return null;
            return cb.like(cb.lower(root.get("productTitle")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<ServiceProduct> hasUrgency(String urgency) {
        return (root, query, cb) -> {
            if (urgency == null || urgency.isBlank()) return null;
            return cb.equal(root.get("urgentTypes"), UrgencyType.valueOf(urgency));
        };
    }
}
