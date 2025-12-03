package com.kudiapp.kudiapp.services.productService;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.productService.ServiceProductCreationRequest;
import com.kudiapp.kudiapp.enums.productService.Category;
import com.kudiapp.kudiapp.enums.productService.PRODUCT_TITLE;
import com.kudiapp.kudiapp.enums.productService.ServiceProductStatus;
import com.kudiapp.kudiapp.enums.productService.UrgencyType;
import org.springframework.data.domain.Pageable;

public interface ServiceProductService {

    GenericResponse createServiceProduct(ServiceProductCreationRequest request);

    GenericResponse updateServiceProduct(Long id, ServiceProductCreationRequest request);

    GenericResponse getServiceProductById(Long id);

    GenericResponse deleteServiceProduct(Long id);

    GenericResponse findAllServiceProducts(
            Category category,
            PRODUCT_TITLE title,
            UrgencyType urgency,
            ServiceProductStatus status,
            Pageable pageable
    );

    GenericResponse findPublicServiceProducts(
            Category category,
            PRODUCT_TITLE title,
            UrgencyType urgency,
            Pageable pageable
    );

    GenericResponse activateServiceProduct(Long id);

    GenericResponse deactivateServiceProduct(Long id);

    GenericResponse archiveServiceProduct(Long id);

    GenericResponse checkActivationEligibility(Long id);
}
