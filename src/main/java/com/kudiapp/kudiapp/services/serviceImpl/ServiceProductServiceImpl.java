package com.kudiapp.kudiapp.services.serviceImpl;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.request.SericeProduct.ServiceProductCreationRequest;
import com.kudiapp.kudiapp.dto.response.SericeProduct.ServiceProductResponse;
import com.kudiapp.kudiapp.dto.response.specifications.ServiceProductSpecifications;
import com.kudiapp.kudiapp.exceptions.ResourceNotFoundException;
import com.kudiapp.kudiapp.models.ServiceProduct;
import com.kudiapp.kudiapp.repository.ServiceProductRepository;
import com.kudiapp.kudiapp.services.ServiceProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceProductServiceImpl implements ServiceProductService {

    private final ServiceProductRepository repository;

    @Override
    public GenericResponse createServiceProduct(ServiceProductCreationRequest request) {
        log.info("Creating ServiceProduct: {}", request.getProductTitle());
        ServiceProduct product = new ServiceProduct();
        applyRequest(product, request);
        product = repository.save(product);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Product created successfully")
                .httpStatus(HttpStatus.CREATED)
                .data(mapToResponse(product))
                .build();
    }

    @Override
    public GenericResponse updateServiceProduct(Long id, ServiceProductCreationRequest request) {
        log.info("Updating ServiceProduct id={}", id);

        ServiceProduct product = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        applyRequest(product, request);

        product = repository.save(product);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Product updated successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponse(product))
                .build();
    }

    @Override
    public GenericResponse getServiceProductById(Long id) {
        log.info("Fetching ServiceProduct id={}", id);

        ServiceProduct product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return GenericResponse.builder()
                .isSuccess(true)
                .httpStatus(HttpStatus.OK)
                .data(mapToResponse(product))
                .build();
    }


    @Override
    public GenericResponse deleteServiceProduct(Long id) {
        log.info("Deleting ServiceProduct id={}", id);
        if (!repository.existsById(id)) {
            return new GenericResponse(false, "Product not found", HttpStatus.NOT_FOUND);
        }

        repository.deleteById(id);
        return GenericResponse.builder()
                .isSuccess(true)
                .message("Product deleted successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Override
    public GenericResponse findAllServiceProduct(String category, String title, String urgency, Pageable pageable) {
        log.info("Fetching products with filters: category={}, title={}, urgency={}", category, title, urgency);

        Specification<ServiceProduct> spec = Specification
                .where(ServiceProductSpecifications.hasCategory(category))
                .and(ServiceProductSpecifications.hasTitle(title))
                .and(ServiceProductSpecifications.hasUrgency(urgency));

        Page<ServiceProduct> products = repository.findAll(spec, pageable);
        Page<ServiceProductResponse> mapped = products.map(this::mapToResponse);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Products fetched successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapped)
                .build();
    }

    private void applyRequest(ServiceProduct product, ServiceProductCreationRequest request) {
        product.setCategory(request.getCategory());
        product.setCategoryItems(request.getCategoryItems());
        product.setProductTitle(request.getProductTitle());
        product.setProductDescription(request.getProductDescription());
        product.setUrgentTypes(request.getUrgentTypes());
        product.setMetadata(request.getMetadata());
    }

    private ServiceProductResponse mapToResponse(ServiceProduct product) {
        ServiceProductResponse response = new ServiceProductResponse();
        response.setId(product.getId());
        response.setCategory(product.getCategory());
        response.setCategoryItems(product.getCategoryItems());
        response.setProductTitle(product.getProductTitle());
        response.setProductDescription(product.getProductDescription());
        response.setUrgentTypes(product.getUrgentTypes());
        response.setMetadata(product.getMetadata());
        return response;
    }
}
