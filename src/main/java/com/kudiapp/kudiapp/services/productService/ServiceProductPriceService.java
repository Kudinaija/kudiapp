package com.kudiapp.kudiapp.services.productService;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.productService.ServiceProductPriceRequest;

public interface ServiceProductPriceService {

    GenericResponse createOrUpdateProductPrice(ServiceProductPriceRequest request);

    GenericResponse getProductPriceByPlanId(Long servicePlanId);

    GenericResponse refreshProductPriceRate(Long priceId);

    GenericResponse deleteProductPrice(Long id);
}