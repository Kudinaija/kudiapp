package com.kudiapp.kudiapp.repository;

import com.kudiapp.kudiapp.models.productService.ServiceProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceProductPriceRepository extends JpaRepository<ServiceProductPrice, Long> {

    Optional<ServiceProductPrice> findByServicePlanId(Long servicePlanId);

    @Query("SELECT spp FROM ServiceProductPrice spp " +
           "WHERE spp.servicePlan.id = :planId " +
           "AND spp.servicePlan.status = 'ACTIVE'")
    Optional<ServiceProductPrice> findActiveProductPriceByPlanId(@Param("planId") Long planId);

    boolean existsByServicePlanId(Long servicePlanId);
}