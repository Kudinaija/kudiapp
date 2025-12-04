package com.kudiapp.kudiapp.services.serviceImpl.productSeervice;


import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.productService.ServiceProductCreationRequest;
import com.kudiapp.kudiapp.dto.productService.ServiceProductResponse;
import com.kudiapp.kudiapp.enums.productService.Category;
import com.kudiapp.kudiapp.enums.productService.PRODUCT_TITLE;
import com.kudiapp.kudiapp.enums.productService.ServiceProductStatus;
import com.kudiapp.kudiapp.enums.productService.UrgencyType;
import com.kudiapp.kudiapp.exceptions.ResourceNotFoundException;
import com.kudiapp.kudiapp.models.productService.ServiceProduct;
import com.kudiapp.kudiapp.repository.ServiceProductRepository;
import com.kudiapp.kudiapp.services.productService.ProductValidationService;
import com.kudiapp.kudiapp.services.productService.ServiceProductService;
import com.kudiapp.kudiapp.services.productService.ServiceProductSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ServiceProductServiceImpl implements ServiceProductService {

    private final ServiceProductRepository serviceProductRepository;
    private final ProductValidationService productValidationService;

    @Override
    public GenericResponse createServiceProduct(ServiceProductCreationRequest request) {
        log.info("Creating service product with title: {}", request.getProductTitle());

        ServiceProduct product = buildServiceProductFromRequest(new ServiceProduct(), request);
        ServiceProduct savedProduct = serviceProductRepository.save(product);

        log.info("Successfully created service product with ID: {} in status: {}", 
                savedProduct.getId(), savedProduct.getStatus());

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Service product created successfully in " + savedProduct.getStatus() + " status")
                .httpStatus(HttpStatus.CREATED)
                .data(mapToResponse(savedProduct))
                .build();
    }

    @Override
    public GenericResponse updateServiceProduct(Long id, ServiceProductCreationRequest request) {
        log.info("Updating service product with ID: {}", id);

        ServiceProduct product = findServiceProductById(id);
        buildServiceProductFromRequest(product, request);
        ServiceProduct updatedProduct = serviceProductRepository.save(product);

        log.info("Successfully updated service product with ID: {}", id);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Service product updated successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponse(updatedProduct))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GenericResponse getServiceProductById(Long id) {
        log.info("Retrieving service product with ID: {}", id);

        ServiceProduct product = findServiceProductById(id);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Service product retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponse(product))
                .build();
    }

    @Override
    public GenericResponse deleteServiceProduct(Long id) {
        log.info("Deleting service product with ID: {}", id);

        ServiceProduct product = findServiceProductById(id);
        serviceProductRepository.delete(product);

        log.info("Successfully deleted service product with ID: {}", id);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Service product deleted successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GenericResponse findAllServiceProducts(
            Category category,
            PRODUCT_TITLE title,
            UrgencyType urgency,
            ServiceProductStatus status,
            Pageable pageable) {
        
        log.info("Fetching service products with filters - category: {}, title: {}, urgency: {}, status: {}", 
                category, title, urgency, status);

        Specification<ServiceProduct> spec = Specification
                .where(ServiceProductSpecification.hasCategory(category))
                .and(ServiceProductSpecification.hasProductTitle(title))
                .and(ServiceProductSpecification.hasUrgencyType(urgency))
                .and(ServiceProductSpecification.hasStatus(status));

        Page<ServiceProduct> products = serviceProductRepository.findAll(spec, pageable);
        Page<ServiceProductResponse> responses = products.map(this::mapToResponse);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Service products retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(responses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GenericResponse findPublicServiceProducts(
            Category category,
            PRODUCT_TITLE title,
            UrgencyType urgency,
            Pageable pageable) {
        
        log.info("Fetching public service products with filters - category: {}, title: {}, urgency: {}", 
                category, title, urgency);

        Specification<ServiceProduct> spec = Specification
                .where(ServiceProductSpecification.hasCategory(category))
                .and(ServiceProductSpecification.hasProductTitle(title))
                .and(ServiceProductSpecification.hasUrgencyType(urgency))
                .and(ServiceProductSpecification.hasStatus(ServiceProductStatus.ACTIVE));

        Page<ServiceProduct> products = serviceProductRepository.findAll(spec, pageable);
        Page<ServiceProductResponse> responses = products.map(this::mapToResponsePublic);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Public service products retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(responses)
                .build();
    }

    @Override
    public GenericResponse activateServiceProduct(Long id) {
        log.info("Activating service product with ID: {}", id);

        ServiceProduct product = findServiceProductById(id);
        
        productValidationService.validateProductForActivation(product);
        
        product.setStatus(ServiceProductStatus.ACTIVE);
        serviceProductRepository.save(product);

        log.info("Successfully activated service product with ID: {}", id);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Service product activated successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponse(product))
                .build();
    }

    @Override
    public GenericResponse deactivateServiceProduct(Long id) {
        log.info("Deactivating service product with ID: {}", id);

        ServiceProduct product = findServiceProductById(id);
        product.setStatus(ServiceProductStatus.INACTIVE);
        serviceProductRepository.save(product);

        log.info("Successfully deactivated service product with ID: {}", id);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Service product deactivated successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponse(product))
                .build();
    }

    @Override
    public GenericResponse archiveServiceProduct(Long id) {
        log.info("Archiving service product with ID: {}", id);

        ServiceProduct product = findServiceProductById(id);
        product.setStatus(ServiceProductStatus.ARCHIVED);
        serviceProductRepository.save(product);

        log.info("Successfully archived service product with ID: {}", id);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Service product archived successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponse(product))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GenericResponse checkActivationEligibility(Long id) {
        log.info("Checking activation eligibility for service product ID: {}", id);

        ServiceProduct product = findServiceProductById(id);
        
        boolean canActivate = productValidationService.canProductBeActivated(product);
        String blockerMessage = productValidationService.getActivationBlockerMessage(product);

        Map<String, Object> eligibilityInfo = new HashMap<>();
        eligibilityInfo.put("productId", id);
        eligibilityInfo.put("currentStatus", product.getStatus());
        eligibilityInfo.put("canBeActivated", canActivate);
        eligibilityInfo.put("hasValidPlans", productValidationService.hasValidPlans(product));
        eligibilityInfo.put("hasValidPricing", productValidationService.hasValidPricing(product));
        eligibilityInfo.put("blockerMessage", blockerMessage);

        return GenericResponse.builder()
                .isSuccess(true)
                .message(canActivate ? "Product is eligible for activation" : "Product cannot be activated")
                .httpStatus(HttpStatus.OK)
                .data(eligibilityInfo)
                .build();
    }

    private ServiceProduct findServiceProductById(Long id) {
        return serviceProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service product not found with ID: " + id));
    }

    private ServiceProduct buildServiceProductFromRequest(
            ServiceProduct product, 
            ServiceProductCreationRequest request) {
        
        product.setCategory(request.getCategory());
        product.setCategoryItems(request.getCategoryItems());
        product.setProductTitle(request.getProductTitle());
        product.setProductDescription(request.getProductDescription());
        product.setUrgencyType(request.getUrgencyType());
        product.setMetadata(request.getMetadata());
        
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        } else if (product.getStatus() == null) {
            product.setStatus(ServiceProductStatus.DRAFT);
        }
        
        return product;
    }

    private ServiceProductResponse mapToResponse(ServiceProduct product) {
        boolean canActivate = productValidationService.canProductBeActivated(product);
        String blockerMessage = productValidationService.getActivationBlockerMessage(product);

        return ServiceProductResponse.builder()
                .id(product.getId())
                .category(product.getCategory())
                .categoryItems(product.getCategoryItems())
                .productTitle(product.getProductTitle())
                .productDescription(product.getProductDescription())
                .urgencyType(product.getUrgencyType())
                .status(product.getStatus())
                .metadata(product.getMetadata())
                .canBeActivated(canActivate)
                .activationBlockerMessage(blockerMessage)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private ServiceProductResponse mapToResponsePublic(ServiceProduct product) {
        return ServiceProductResponse.builder()
                .id(product.getId())
                .category(product.getCategory())
                .categoryItems(product.getCategoryItems())
                .productTitle(product.getProductTitle())
                .productDescription(product.getProductDescription())
                .urgencyType(product.getUrgencyType())
                .status(product.getStatus())
                .metadata(product.getMetadata())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
