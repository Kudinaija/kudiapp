package com.kudiapp.kudiapp.services.productService;

import com.kudiapp.kudiapp.enums.productService.Category;
import com.kudiapp.kudiapp.enums.productService.PRODUCT_TITLE;
import com.kudiapp.kudiapp.enums.productService.ServiceProductStatus;
import com.kudiapp.kudiapp.enums.productService.UrgencyType;
import com.kudiapp.kudiapp.models.productService.ServiceProduct;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

/**
 * JPA Specifications for filtering ServiceProduct entities
 * These specifications are used to build dynamic queries with optional filters
 */
public class ServiceProductSpecification {

    /**
     * Filter by category
     * Returns all products if category is null
     */
    public static Specification<ServiceProduct> hasCategory(Category category) {
        return (Root<ServiceProduct> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (category == null) {
                return cb.conjunction(); // Return "true" predicate (no filter)
            }
            return cb.equal(root.get("category"), category);
        };
    }

    /**
     * Filter by product title
     * Returns all products if title is null
     */
    public static Specification<ServiceProduct> hasProductTitle(PRODUCT_TITLE title) {
        return (Root<ServiceProduct> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (title == null) {
                return cb.conjunction(); // Return "true" predicate (no filter)
            }
            return cb.equal(root.get("productTitle"), title);
        };
    }

    /**
     * Filter by urgency type
     * Returns all products if urgencyType is null
     */
    public static Specification<ServiceProduct> hasUrgencyType(UrgencyType urgencyType) {
        return (Root<ServiceProduct> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (urgencyType == null) {
                return cb.conjunction(); // Return "true" predicate (no filter)
            }
            return cb.equal(root.get("urgencyType"), urgencyType);
        };
    }

    /**
     * Filter by status
     * Returns all products if status is null
     */
    public static Specification<ServiceProduct> hasStatus(ServiceProductStatus status) {
        return (Root<ServiceProduct> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (status == null) {
                return cb.conjunction(); // Return "true" predicate (no filter)
            }
            return cb.equal(root.get("status"), status);
        };
    }

    /**
     * Filter for publicly visible products (status = ACTIVE)
     * This is used for customer-facing endpoints
     */
    public static Specification<ServiceProduct> isPubliclyVisible() {
        return (Root<ServiceProduct> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
                cb.equal(root.get("status"), ServiceProductStatus.ACTIVE);
    }

    /**
     * Filter by multiple statuses (OR condition)
     * Useful for admin dashboards showing different product states
     */
    public static Specification<ServiceProduct> hasAnyStatus(ServiceProductStatus... statuses) {
        return (Root<ServiceProduct> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (statuses == null || statuses.length == 0) {
                return cb.conjunction();
            }
            Predicate[] predicates = new Predicate[statuses.length];
            for (int i = 0; i < statuses.length; i++) {
                predicates[i] = cb.equal(root.get("status"), statuses[i]);
            }
            return cb.or(predicates);
        };
    }

    /**
     * Combine multiple filters using AND logic
     * Example usage:
     * Specification<ServiceProduct> spec = hasCategory(Category.EDUCATION)
     *     .and(hasStatus(ServiceProductStatus.ACTIVE))
     *     .and(hasUrgencyType(UrgencyType.URGENT));
     */
    public static Specification<ServiceProduct> combineFilters(
            Category category,
            PRODUCT_TITLE title,
            UrgencyType urgencyType,
            ServiceProductStatus status) {

        return Specification
                .where(hasCategory(category))
                .and(hasProductTitle(title))
                .and(hasUrgencyType(urgencyType))
                .and(hasStatus(status));
    }
}