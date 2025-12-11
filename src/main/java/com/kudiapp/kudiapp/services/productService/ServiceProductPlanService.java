package com.kudiapp.kudiapp.services.productService;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.productService.ServiceProductPlanRequest;
import com.kudiapp.kudiapp.enums.productService.ServiceProductPlanStatus;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing service product plans
 */
public interface ServiceProductPlanService {

    /**
     * Create a new product plan
     * 
     * @param request Product plan creation request
     * @return GenericResponse containing the created plan
     */
    GenericResponse createProductPlan(ServiceProductPlanRequest request);

    /**
     * Update an existing product plan
     * 
     * @param id Plan ID
     * @param request Updated plan data
     * @return GenericResponse containing the updated plan
     */
    GenericResponse updateProductPlan(Long id, ServiceProductPlanRequest request);

    /**
     * Get product plan by ID
     * 
     * @param id Plan ID
     * @return GenericResponse containing plan details
     */
    GenericResponse getProductPlanById(Long id);

    /**
     * Get all plans for a specific service product
     * 
     * @param serviceProductId Service product ID
     * @param status Optional status filter
     * @return GenericResponse containing list of plans
     */
    GenericResponse getProductPlansByServiceProduct(Long serviceProductId, ServiceProductPlanStatus status);

    /**
     * Get active plans for a service product (user-facing)
     * 
     * @param serviceProductId Service product ID
     * @return GenericResponse containing active plans
     */
    GenericResponse getActiveProductPlans(Long serviceProductId);

    /**
     * Get all product plans with filters (admin)
     * 
     * @param status Optional status filter
     * @param isFeatured Optional featured filter
     * @param pageable Pagination parameters
     * @return GenericResponse containing paginated plans
     */
    GenericResponse getAllProductPlans(
            ServiceProductPlanStatus status,
            Boolean isFeatured,
            Pageable pageable
    );

    /**
     * Activate a product plan
     * 
     * @param id Plan ID
     * @return GenericResponse with activation confirmation
     */
    GenericResponse activateProductPlan(Long id);

    /**
     * Deactivate a product plan
     * 
     * @param id Plan ID
     * @return GenericResponse with deactivation confirmation
     */
    GenericResponse deactivateProductPlan(Long id);

    /**
     * Suspend a product plan
     * 
     * @param id Plan ID
     * @return GenericResponse with suspension confirmation
     */
    GenericResponse suspendProductPlan(Long id);

    /**
     * Toggle featured status of a plan
     * 
     * @param id Plan ID
     * @param featured Featured status
     * @return GenericResponse with update confirmation
     */
    GenericResponse toggleFeaturedStatus(Long id, Boolean featured);

    /**
     * Update display order of a plan
     * 
     * @param id Plan ID
     * @param displayOrder New display order
     * @return GenericResponse with update confirmation
     */
    GenericResponse updateDisplayOrder(Long id, Integer displayOrder);

    /**
     * Delete a product plan
     * 
     * @param id Plan ID
     * @return GenericResponse with deletion confirmation
     */
    GenericResponse deleteProductPlan(Long id);

    /**
     * Get all featured plans
     * 
     * @return GenericResponse containing featured plans
     */
    GenericResponse getFeaturedPlans();
}