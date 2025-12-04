package com.kudiapp.kudiapp.services.productService;


import com.kudiapp.kudiapp.models.productService.ServiceProduct;

public interface ProductValidationService {

    boolean canProductBeActivated(ServiceProduct product);

    String getActivationBlockerMessage(ServiceProduct product);

    void validateProductForActivation(ServiceProduct product);

    boolean hasValidPlans(ServiceProduct product);

    boolean hasValidPricing(ServiceProduct product);
}
