package com.kudiapp.kudiapp.services.serviceImpl.productSeervice;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.productService.ServiceProductPlanRequest;
import com.kudiapp.kudiapp.dto.productService.ServiceProductPlanResponse;
import com.kudiapp.kudiapp.enums.productService.ServiceProductPlanStatus;
import com.kudiapp.kudiapp.enums.productService.ServiceProductPlanType;
import com.kudiapp.kudiapp.exceptions.InvalidOperationException;
import com.kudiapp.kudiapp.exceptions.ResourceAlreadyExistsException;
import com.kudiapp.kudiapp.exceptions.ResourceNotFoundException;
import com.kudiapp.kudiapp.models.productService.ServiceProduct;
import com.kudiapp.kudiapp.models.productService.ServiceProductPlan;
import com.kudiapp.kudiapp.repository.OrderRepository;
import com.kudiapp.kudiapp.repository.ServiceProductPlanRepository;
import com.kudiapp.kudiapp.repository.ServiceProductRepository;
import com.kudiapp.kudiapp.services.productService.ServiceProductPlanService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ServiceProductPlanService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ServiceProductPlanServiceImpl implements ServiceProductPlanService {

    private final ServiceProductPlanRepository planRepository;
    private final ServiceProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    @SneakyThrows
    public GenericResponse createProductPlan(ServiceProductPlanRequest request) {
        log.info("Creating product plan '{}' for service product ID: {}", 
                request.getPlanName(), request.getServiceProductId());

        // Validate service product exists
        ServiceProduct serviceProduct = findServiceProductById(request.getServiceProductId());

        // Check if plan name already exists for this product
        if (planRepository.existsByPlanNameAndServiceProductId(
                request.getPlanName(), 
                request.getServiceProductId())) {
            log.error("Plan name '{}' already exists for product ID: {}", 
                    request.getPlanName(), request.getServiceProductId());
            throw new ResourceAlreadyExistsException(
                    "A plan with name '" + request.getPlanName() + 
                    "' already exists for this service product"
            );
        }

        // Build and save plan
        ServiceProductPlan plan = buildProductPlanFromRequest(new ServiceProductPlan(), request);
        plan.setServiceProduct(serviceProduct);
        
        ServiceProductPlan savedPlan = planRepository.save(plan);
        log.info("Successfully created product plan with ID: {}", savedPlan.getId());

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Product plan created successfully")
                .httpStatus(HttpStatus.CREATED)
                .data(mapToResponse(savedPlan))
                .build();
    }

    @Override
    @SneakyThrows
    public GenericResponse updateProductPlan(Long id, ServiceProductPlanRequest request) {
        log.info("Updating product plan with ID: {}", id);

        ServiceProductPlan plan = findProductPlanById(id);

        // Check if changing plan name and if new name already exists
        if (!plan.getPlanName().equals(request.getPlanName())) {
            if (planRepository.existsByPlanNameAndServiceProductId(
                    request.getPlanName(), 
                    plan.getServiceProduct().getId())) {
                log.error("Plan name '{}' already exists for this product", request.getPlanName());
                throw new ResourceAlreadyExistsException(
                        "A plan with name '" + request.getPlanName() + 
                        "' already exists for this service product"
                );
            }
        }

        buildProductPlanFromRequest(plan, request);
        ServiceProductPlan updatedPlan = planRepository.save(plan);

        log.info("Successfully updated product plan with ID: {}", id);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Product plan updated successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponse(updatedPlan))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GenericResponse getProductPlanById(Long id) {
        log.info("Retrieving product plan with ID: {}", id);

        ServiceProductPlan plan = findProductPlanById(id);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Product plan retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponse(plan))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GenericResponse getProductPlansByServiceProduct(
            Long serviceProductId, 
            ServiceProductPlanStatus status) {
        
        log.info("Retrieving plans for service product ID: {} with status: {}", 
                serviceProductId, status);

        // Validate service product exists
        findServiceProductById(serviceProductId);

        List<ServiceProductPlan> plans;
        
        if (status != null) {
            plans = planRepository.findByServiceProductIdAndStatus(serviceProductId, status);
        } else {
            plans = planRepository.findByServiceProductId(serviceProductId);
        }

        List<ServiceProductPlanResponse> responses = plans.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Found {} plans for service product ID: {}", plans.size(), serviceProductId);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Product plans retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(responses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GenericResponse getActiveProductPlans(Long serviceProductId) {
        log.info("Retrieving active plans for service product ID: {}", serviceProductId);

        // Validate service product exists
        findServiceProductById(serviceProductId);

        List<ServiceProductPlan> activePlans = planRepository.findActiveProductPlansOrdered(
                serviceProductId,
                ServiceProductPlanStatus.ACTIVE
        );

        List<ServiceProductPlanResponse> responses = activePlans.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Found {} active plans for service product ID: {}", 
                activePlans.size(), serviceProductId);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Active product plans retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(responses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GenericResponse getAllProductPlans(
            ServiceProductPlanStatus status,
            Boolean isFeatured,
            Pageable pageable) {
        
        log.info("Retrieving all plans with filters - status: {}, featured: {}", 
                status, isFeatured);

        Page<ServiceProductPlan> plans;

        // Apply filters
        if (status != null && isFeatured != null) {
            plans = planRepository.findAll(
                    (root, query, cb) -> cb.and(
                            cb.equal(root.get("status"), status),
                            cb.equal(root.get("isFeatured"), isFeatured)
                    ),
                    pageable
            );
        } else if (status != null) {
            plans = planRepository.findAll(
                    (root, query, cb) -> cb.equal(root.get("status"), status),
                    pageable
            );
        } else if (isFeatured != null) {
            plans = planRepository.findAll(
                    (root, query, cb) -> cb.equal(root.get("isFeatured"), isFeatured),
                    pageable
            );
        } else {
            plans = planRepository.findAll(pageable);
        }

        Page<ServiceProductPlanResponse> responses = plans.map(this::mapToResponse);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Product plans retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(responses)
                .build();
    }

    @Override
    public GenericResponse activateProductPlan(Long id) {
        log.info("Activating product plan with ID: {}", id);

        ServiceProductPlan plan = findProductPlanById(id);
        plan.setStatus(ServiceProductPlanStatus.ACTIVE);
        
        ServiceProductPlan updatedPlan = planRepository.save(plan);
        log.info("Successfully activated product plan with ID: {}", id);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Product plan activated successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponse(updatedPlan))
                .build();
    }

    @Override
    public GenericResponse deactivateProductPlan(Long id) {
        log.info("Deactivating product plan with ID: {}", id);

        ServiceProductPlan plan = findProductPlanById(id);
        plan.setStatus(ServiceProductPlanStatus.DEACTIVATED);
        
        ServiceProductPlan updatedPlan = planRepository.save(plan);
        log.info("Successfully deactivated product plan with ID: {}", id);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Product plan deactivated successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponse(updatedPlan))
                .build();
    }

    @Override
    public GenericResponse suspendProductPlan(Long id) {
        log.info("Suspending product plan with ID: {}", id);

        ServiceProductPlan plan = findProductPlanById(id);
        plan.setStatus(ServiceProductPlanStatus.SUSPENDED);
        
        ServiceProductPlan updatedPlan = planRepository.save(plan);
        log.info("Successfully suspended product plan with ID: {}", id);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Product plan suspended successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponse(updatedPlan))
                .build();
    }

    @Override
    public GenericResponse toggleFeaturedStatus(Long id, Boolean featured) {
        log.info("Setting featured status to {} for product plan ID: {}", featured, id);

        ServiceProductPlan plan = findProductPlanById(id);
        plan.setIsFeatured(featured);
        
        ServiceProductPlan updatedPlan = planRepository.save(plan);
        log.info("Successfully updated featured status for plan ID: {}", id);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Featured status updated successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponse(updatedPlan))
                .build();
    }

    @Override
    public GenericResponse updateDisplayOrder(Long id, Integer displayOrder) {
        log.info("Updating display order to {} for product plan ID: {}", displayOrder, id);

        ServiceProductPlan plan = findProductPlanById(id);
        plan.setDisplayOrder(displayOrder);
        
        ServiceProductPlan updatedPlan = planRepository.save(plan);
        log.info("Successfully updated display order for plan ID: {}", id);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Display order updated successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponse(updatedPlan))
                .build();
    }

    @Override
    @SneakyThrows
    public GenericResponse deleteProductPlan(Long id) {
        log.info("Deleting product plan with ID: {}", id);

        ServiceProductPlan plan = findProductPlanById(id);

        // Check if plan has any orders
        long orderCount = orderRepository.countByServicePlanId(id);
        if (orderCount > 0) {
            log.error("Cannot delete plan ID {} - has {} existing orders", id, orderCount);
            throw new InvalidOperationException(
                    "Cannot delete plan with existing orders. Found " + orderCount + " order(s)."
            );
        }

        planRepository.delete(plan);
        log.info("Successfully deleted product plan with ID: {}", id);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Product plan deleted successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GenericResponse getFeaturedPlans() {
        log.info("Retrieving all featured plans");

        List<ServiceProductPlan> featuredPlans = planRepository.findAll(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("isFeatured"), true),
                        cb.equal(root.get("status"), ServiceProductPlanStatus.ACTIVE)
                )
        );

        List<ServiceProductPlanResponse> responses = featuredPlans.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Found {} featured plans", featuredPlans.size());

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Featured plans retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(responses)
                .build();
    }

    // PRIVATE HELPER METHODS

    private ServiceProduct findServiceProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Service product not found with ID: {}", id);
                    return new ResourceNotFoundException(
                            "Service product not found with ID: " + id);
                });
    }

    private ServiceProductPlan findProductPlanById(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product plan not found with ID: {}", id);
                    return new ResourceNotFoundException(
                            "Product plan not found with ID: " + id);
                });
    }

    private ServiceProductPlan buildProductPlanFromRequest(
            ServiceProductPlan plan,
            ServiceProductPlanRequest request) {
        
        plan.setPlanName(request.getPlanName());
        plan.setAmount(request.getAmount());
        plan.setCurrency(request.getCurrency());
        plan.setPlanDescription(request.getPlanDescription());
        plan.setDisplayOrder(request.getDisplayOrder());
        
        if (request.getStatus() != null) {
            plan.setStatus(request.getStatus());
        } else if (plan.getStatus() == null) {
            plan.setStatus(ServiceProductPlanStatus.ACTIVE);
        }
        
        if (request.getPlanType() != null) {
            plan.setPlanType(request.getPlanType());
        } else if (plan.getPlanType() == null) {
            plan.setPlanType(ServiceProductPlanType.DEFAULT);
        }
        
        if (request.getIsFeatured() != null) {
            plan.setIsFeatured(request.getIsFeatured());
        } else if (plan.getIsFeatured() == null) {
            plan.setIsFeatured(false);
        }

        return plan;
    }

    private ServiceProductPlanResponse mapToResponse(ServiceProductPlan plan) {
        return ServiceProductPlanResponse.builder()
                .id(plan.getId())
                .planName(plan.getPlanName())
                .serviceProductId(plan.getServiceProduct().getId())
                .serviceProductTitle(plan.getServiceProduct().getProductTitle().getDescription())
                .amount(plan.getAmount())
                .currency(plan.getCurrency())
                .status(plan.getStatus())
                .planType(plan.getPlanType())
                .planDescription(plan.getPlanDescription())
                .displayOrder(plan.getDisplayOrder())
                .isFeatured(plan.getIsFeatured())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }
}