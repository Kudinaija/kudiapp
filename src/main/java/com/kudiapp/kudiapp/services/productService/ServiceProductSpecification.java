package com.kudiapp.kudiapp.services.productService;


import com.kudiapp.kudiapp.enums.productService.Category;
import com.kudiapp.kudiapp.enums.productService.PRODUCT_TITLE;
import com.kudiapp.kudiapp.enums.productService.ServiceProductStatus;
import com.kudiapp.kudiapp.enums.productService.UrgencyType;
import com.kudiapp.kudiapp.models.productService.ServiceProduct;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class ServiceProductSpecification {

    public static Specification<ServiceProduct> hasCategory(Category category) {
        return (Root<ServiceProduct> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> 
            category == null ? cb.conjunction() : cb.equal(root.get("category"), category);
    }

    public static Specification<ServiceProduct> hasProductTitle(PRODUCT_TITLE title) {
        return (Root<ServiceProduct> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> 
            title == null ? cb.conjunction() : cb.equal(root.get("productTitle"), title);
    }

    public static Specification<ServiceProduct> hasUrgencyType(UrgencyType urgencyType) {
        return (Root<ServiceProduct> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> 
            urgencyType == null ? cb.conjunction() : cb.equal(root.get("urgencyType"), urgencyType);
    }

    public static Specification<ServiceProduct> hasStatus(ServiceProductStatus status) {
        return (Root<ServiceProduct> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> 
            status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<ServiceProduct> isPubliclyVisible() {
        return (Root<ServiceProduct> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> 
            cb.equal(root.get("status"), ServiceProductStatus.ACTIVE);
    }
}
