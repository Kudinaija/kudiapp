package com.kudiapp.kudiapp.controller;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.productService.ServiceProductPriceRequest;
import com.kudiapp.kudiapp.services.productService.ServiceProductPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/product-prices")
@RequiredArgsConstructor
@Tag(name = "Product Price Management", description = "APIs for managing product prices with automatic currency conversion")
public class ServiceProductPriceController {

    private final ServiceProductPriceService productPriceService;

    @PostMapping
    @Operation(summary = "Create or update product price", 
               description = "Creates or updates a product price with automatic currency conversion based on exchange rates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product price created successfully",
                    content = @Content(schema = @Schema(implementation = GenericResponse.class))),
            @ApiResponse(responseCode = "200", description = "Product price updated successfully"),
            @ApiResponse(responseCode = "404", description = "Service plan not found")
    })
    public ResponseEntity<GenericResponse> createOrUpdateProductPrice(
            @Valid @RequestBody ServiceProductPriceRequest request) {
        GenericResponse response = productPriceService.createOrUpdateProductPrice(request);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping("/plan/{servicePlanId}")
    @Operation(summary = "Get product price by plan ID", description = "Retrieves the product price for a specific service plan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product price retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product price not found")
    })
    public ResponseEntity<GenericResponse> getProductPriceByPlanId(
            @Parameter(description = "Service plan ID") @PathVariable Long servicePlanId) {
        GenericResponse response = productPriceService.getProductPriceByPlanId(servicePlanId);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PatchMapping("/{id}/refresh-rate")
    @Operation(summary = "Refresh conversion rate", 
               description = "Refreshes the currency conversion rate and recalculates the amount to pay")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversion rate refreshed successfully"),
            @ApiResponse(responseCode = "404", description = "Product price not found")
    })
    public ResponseEntity<GenericResponse> refreshProductPriceRate(
            @Parameter(description = "Product price ID") @PathVariable Long id) {
        GenericResponse response = productPriceService.refreshProductPriceRate(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product price", description = "Permanently deletes a product price")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product price deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product price not found")
    })
    public ResponseEntity<GenericResponse> deleteProductPrice(
            @Parameter(description = "Product price ID") @PathVariable Long id) {
        GenericResponse response = productPriceService.deleteProductPrice(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}