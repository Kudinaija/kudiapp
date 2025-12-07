package com.kudiapp.kudiapp.services.serviceImpl.productSeervice;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.productService.ServiceProductPriceRequest;
import com.kudiapp.kudiapp.dto.productService.ServiceProductPriceResponse;
import com.kudiapp.kudiapp.enums.productService.Currency;
import com.kudiapp.kudiapp.exceptions.ResourceNotFoundException;
import com.kudiapp.kudiapp.models.productService.ServiceProductPlan;
import com.kudiapp.kudiapp.models.productService.ServiceProductPrice;
import com.kudiapp.kudiapp.repository.ServiceProductPlanRepository;
import com.kudiapp.kudiapp.repository.ServiceProductPriceRepository;
import com.kudiapp.kudiapp.services.productService.CurrencyExchangeRateService;
import com.kudiapp.kudiapp.services.productService.ServiceProductPriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ServiceProductPriceServiceImpl implements ServiceProductPriceService {

    private final ServiceProductPriceRepository productPriceRepository;
    private final ServiceProductPlanRepository productPlanRepository;
    private final CurrencyExchangeRateService exchangeRateService;

    @Override
    public GenericResponse createOrUpdateProductPrice(ServiceProductPriceRequest request) {
        log.info("Creating/Updating product price for plan ID: {}", request.getServicePlanId());

        ServiceProductPlan servicePlan = findServicePlanById(request.getServicePlanId());
        
        ServiceProductPrice price = productPriceRepository
                .findByServicePlanId(request.getServicePlanId())
                .orElse(new ServiceProductPrice());

        buildProductPriceFromRequest(price, request, servicePlan);
        
        BigDecimal conversionRate = fetchConversionRate(
                request.getDefaultCurrency(), 
                request.getAmountCurrency()
        );
        
        price.setConversionRate(conversionRate);
        price.setRateTimestamp(System.currentTimeMillis());
        price.calculateAmountToPay();
        
        ServiceProductPrice savedPrice = productPriceRepository.save(price);

        log.info("Successfully created/updated product price with ID: {}", savedPrice.getId());

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Product price created/updated successfully")
                .httpStatus(price.getId() == null ? HttpStatus.CREATED : HttpStatus.OK)
                .data(mapToResponse(savedPrice))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GenericResponse getProductPriceByPlanId(Long servicePlanId) {
        log.info("Retrieving product price for plan ID: {}", servicePlanId);

        ServiceProductPrice price = productPriceRepository
                .findByServicePlanId(servicePlanId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product price not found for plan ID: " + servicePlanId));

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Product price retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponse(price))
                .build();
    }

    @Override
    public GenericResponse refreshProductPriceRate(Long priceId) {
        log.info("Refreshing conversion rate for product price ID: {}", priceId);

        ServiceProductPrice price = findProductPriceById(priceId);
        
        BigDecimal newRate = fetchConversionRate(
                price.getDefaultCurrency(),
                price.getAmountCurrency()
        );
        
        price.setConversionRate(newRate);
        price.setRateTimestamp(System.currentTimeMillis());
        price.calculateAmountToPay();
        
        ServiceProductPrice updatedPrice = productPriceRepository.save(price);

        log.info("Successfully refreshed conversion rate for product price ID: {}", priceId);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Product price rate refreshed successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponse(updatedPrice))
                .build();
    }

    @Override
    public GenericResponse deleteProductPrice(Long id) {
        log.info("Deleting product price with ID: {}", id);

        ServiceProductPrice price = findProductPriceById(id);
        productPriceRepository.delete(price);

        log.info("Successfully deleted product price with ID: {}", id);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Product price deleted successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    private ServiceProductPlan findServicePlanById(Long id) {
        return productPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service product plan not found with ID: " + id));
    }

    private ServiceProductPrice findProductPriceById(Long id) {
        return productPriceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product price not found with ID: " + id));
    }

    private BigDecimal fetchConversionRate(Currency fromCurrency, Currency toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return BigDecimal.ONE;
        }
        
        return exchangeRateService.getConversionRate(fromCurrency, toCurrency);
    }

    private void buildProductPriceFromRequest(
            ServiceProductPrice price,
            ServiceProductPriceRequest request,
            ServiceProductPlan servicePlan) {
        
        price.setServicePlan(servicePlan);
        price.setDefaultPrice(request.getDefaultPrice());
        price.setDefaultCurrency(request.getDefaultCurrency());
        price.setAmountCurrency(request.getAmountCurrency());
    }

    private ServiceProductPriceResponse mapToResponse(ServiceProductPrice price) {
        return ServiceProductPriceResponse.builder()
                .id(price.getId())
                .servicePlanId(price.getServicePlan().getId())
                .servicePlanName(price.getServicePlan().getPlanName())
                .defaultPrice(price.getDefaultPrice())
                .defaultCurrency(price.getDefaultCurrency())
                .amountToPay(price.getAmountToPay())
                .amountCurrency(price.getAmountCurrency())
                .conversionRate(price.getConversionRate())
                .rateTimestamp(price.getRateTimestamp())
                .rateSource(price.getRateSource())
                .isRateStale(price.isRateStale())
                .createdAt(price.getCreatedAt())
                .updatedAt(price.getUpdatedAt())
                .build();
    }
}