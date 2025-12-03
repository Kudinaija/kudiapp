package com.kudiapp.kudiapp.repository;

import com.kudiapp.kudiapp.enums.productService.ServiceProductPlanStatus;
import com.kudiapp.kudiapp.enums.productService.ServiceProductPlanType;
import com.kudiapp.kudiapp.models.productService.ServiceProductPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceProductPlanRepository extends 
        JpaRepository<ServiceProductPlan, Long>, JpaSpecificationExecutor<ServiceProductPlan> {

    Optional<ServiceProductPlan> findByIdAndStatus(Long id, ServiceProductPlanStatus status);

    List<ServiceProductPlan> findByServiceProductIdAndStatus(
            Long serviceProductId, 
            ServiceProductPlanStatus status
    );

    List<ServiceProductPlan> findByServiceProductId(Long serviceProductId);

    @Query("SELECT spp FROM ServiceProductPlan spp WHERE spp.serviceProduct.id = :productId " +
           "AND spp.status = :status ORDER BY spp.displayOrder ASC, spp.createdAt DESC")
    List<ServiceProductPlan> findActiveProductPlansOrdered(
            @Param("productId") Long productId,
            @Param("status") ServiceProductPlanStatus status
    );

    Optional<ServiceProductPlan> findByPlanNameAndServiceProductId(
            String planName, 
            Long serviceProductId
    );

    List<ServiceProductPlan> findByPlanTypeAndStatus(
            ServiceProductPlanType planType,
            ServiceProductPlanStatus status
    );

    boolean existsByPlanNameAndServiceProductId(String planName, Long serviceProductId);
}
