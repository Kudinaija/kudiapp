package com.kudiapp.kudiapp.repository;

import com.kudiapp.kudiapp.enums.productService.Category;
import com.kudiapp.kudiapp.enums.productService.PRODUCT_TITLE;
import com.kudiapp.kudiapp.enums.productService.ServiceProductStatus;
import com.kudiapp.kudiapp.models.productService.ServiceProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceProductRepository extends JpaRepository<ServiceProduct, Long>, JpaSpecificationExecutor<ServiceProduct> {

    Optional<ServiceProduct> findByIdAndStatus(Long id, ServiceProductStatus status);

    List<ServiceProduct> findByCategoryAndStatus(Category category, ServiceProductStatus status);

    List<ServiceProduct> findByProductTitleAndStatus(PRODUCT_TITLE productTitle, ServiceProductStatus status);

    Page<ServiceProduct> findByStatus(ServiceProductStatus status, Pageable pageable);

    @Query("SELECT sp FROM ServiceProduct sp WHERE sp.status = :status")
    List<ServiceProduct> findAllByStatus(@Param("status") ServiceProductStatus status);

    @Query("SELECT sp FROM ServiceProduct sp WHERE sp.status = 'ACTIVE'")
    List<ServiceProduct> findAllActiveProducts();

    @Query("SELECT sp FROM ServiceProduct sp WHERE sp.status = 'DRAFT'")
    List<ServiceProduct> findAllDraftProducts();

    boolean existsByProductTitleAndStatus(PRODUCT_TITLE productTitle, ServiceProductStatus status);

    long countByStatus(ServiceProductStatus status);
}