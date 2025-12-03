package com.kudiapp.kudiapp.services.serviceImpl.productSeervice;

import com.kudiapp.kudiapp.enums.productService.ServiceProductPlanStatus;
import com.kudiapp.kudiapp.exceptions.InvalidOperationException;
import com.kudiapp.kudiapp.models.productService.ServiceProduct;
import com.kudiapp.kudiapp.models.productService.ServiceProductPlan;
import com.kudiapp.kudiapp.models.productService.ServiceProductPrice;
import com.kudiapp.kudiapp.services.productService.ProductValidationService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductValidationServiceImpl implements ProductValidationService {

    @Override
    public boolean canProductBeActivated(ServiceProduct product) {
        return hasValidPlans(product) && hasValidPricing(product);
    }

    @Override
    public String getActivationBlockerMessage(ServiceProduct product) {
        List<String> issues = new ArrayList<>();

        if (!hasValidPlans(product)) {
            issues.add("Product must have at least one active plan");
        }

        if (!hasValidPricing(product)) {
            issues.add("All active plans must have valid pricing configured");
        }

        if (issues.isEmpty()) {
            return null;
        }

        return "Cannot activate product: " + String.join("; ", issues);
    }

    @SneakyThrows
    @Override
    public void validateProductForActivation(ServiceProduct product) {
        if (!canProductBeActivated(product)) {
            String message = getActivationBlockerMessage(product);
            log.error("Product activation validation failed for product ID {}: {}", 
                    product.getId(), message);
            throw new InvalidOperationException(message);
        }
    }

    @Override
    public boolean hasValidPlans(ServiceProduct product) {
        if (product.getProductPlans() == null || product.getProductPlans().isEmpty()) {
            return false;
        }

        long activePlanCount = product.getProductPlans().stream()
                .filter(plan -> ServiceProductPlanStatus.ACTIVE.equals(plan.getStatus()))
                .count();

        return activePlanCount > 0;
    }

    @Override
    public boolean hasValidPricing(ServiceProduct product) {
        if (product.getProductPlans() == null || product.getProductPlans().isEmpty()) {
            return false;
        }

        List<ServiceProductPlan> activePlans = product.getProductPlans().stream()
                .filter(plan -> ServiceProductPlanStatus.ACTIVE.equals(plan.getStatus()))
                .toList();

        if (activePlans.isEmpty()) {
            return false;
        }

        for (ServiceProductPlan plan : activePlans) {
            ServiceProductPrice price = plan.getProductPrice();
            
            if (price == null) {
                log.warn("Active plan {} (ID: {}) has no price configured", 
                        plan.getPlanName(), plan.getId());
                return false;
            }

            if (price.getDefaultPrice() == null || price.getAmountToPay() == null) {
                log.warn("Active plan {} (ID: {}) has incomplete pricing", 
                        plan.getPlanName(), plan.getId());
                return false;
            }

            if (price.getConversionRate() == null || price.getConversionRate().signum() <= 0) {
                log.warn("Active plan {} (ID: {}) has invalid conversion rate", 
                        plan.getPlanName(), plan.getId());
                return false;
            }
        }

        return true;
    }
}
