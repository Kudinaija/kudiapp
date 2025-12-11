package com.kudiapp.kudiapp.controller;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.productService.ServiceProductCreationRequest;
import com.kudiapp.kudiapp.enums.productService.Category;
import com.kudiapp.kudiapp.enums.productService.PRODUCT_TITLE;
import com.kudiapp.kudiapp.enums.productService.ServiceProductStatus;
import com.kudiapp.kudiapp.enums.productService.UrgencyType;
import com.kudiapp.kudiapp.services.productService.ServiceProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing service products (Admin operations)
 */
@RestController
@RequestMapping("/api/v1/admin/service-products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Service Product Management", description = "Admin endpoints for managing service products")
public class ServiceProductAdminController {

    private final ServiceProductService serviceProductService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Create a new service product",
            description = "Creates a new service product in DRAFT status. Product can only be activated after adding plans and pricing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Service product created successfully in DRAFT status",
                    content = @Content(schema = @Schema(implementation = GenericResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = GenericResponse.class)))
    })
    public ResponseEntity<GenericResponse> createServiceProduct(
            @Valid @RequestBody ServiceProductCreationRequest request) {
        log.info("Received request to create service product: {}", request.getProductTitle());
        GenericResponse response = serviceProductService.createServiceProduct(request);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Update a service product", description = "Updates an existing service product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service product updated successfully"),
            @ApiResponse(responseCode = "404", description = "Service product not found")
    })
    public ResponseEntity<GenericResponse> updateServiceProduct(
            @Parameter(description = "Service product ID") @PathVariable Long id,
            @Valid @RequestBody ServiceProductCreationRequest request) {
        log.info("Received request to update service product ID: {}", id);
        GenericResponse response = serviceProductService.updateServiceProduct(id, request);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get service product by ID",
            description = "Retrieves a specific service product with activation eligibility information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service product retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Service product not found")
    })
    public ResponseEntity<GenericResponse> getServiceProductById(
            @Parameter(description = "Service product ID") @PathVariable Long id) {
        log.info("Received request to get service product by ID: {}", id);
        GenericResponse response = serviceProductService.getServiceProductById(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all service products (Admin)",
            description = "Retrieves all service products with optional filters. Shows all statuses including DRAFT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service products retrieved successfully")
    })
    public ResponseEntity<GenericResponse> getAllServiceProducts(
            @Parameter(description = "Filter by category") @RequestParam(required = false) Category category,
            @Parameter(description = "Filter by product title") @RequestParam(required = false) PRODUCT_TITLE title,
            @Parameter(description = "Filter by urgency type") @RequestParam(required = false) UrgencyType urgency,
            @Parameter(description = "Filter by status") @RequestParam(required = false) ServiceProductStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("Received request to get all service products with filters - category: {}, title: {}, urgency: {}, status: {}, page: {}, size: {}",
                category, title, urgency, status, pageable.getPageNumber(), pageable.getPageSize());

        GenericResponse response = serviceProductService.findAllServiceProducts(
                category, title, urgency, status, pageable);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping("/public")
    @Operation(summary = "Get public service products (User Facing)",
            description = "Retrieves only ACTIVE service products that are available for ordering by users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Public service products retrieved successfully")
    })
    public ResponseEntity<GenericResponse> getPublicServiceProducts(
            @Parameter(description = "Filter by category") @RequestParam(required = false) Category category,
            @Parameter(description = "Filter by product title") @RequestParam(required = false) PRODUCT_TITLE title,
            @Parameter(description = "Filter by urgency type") @RequestParam(required = false) UrgencyType urgency,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("Received request to get public service products");
        GenericResponse response = serviceProductService.findPublicServiceProducts(
                category, title, urgency, pageable);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Activate service product",
            description = "Activates a service product. Validates that product has at least one active plan with valid pricing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service product activated successfully"),
            @ApiResponse(responseCode = "400", description = "Product cannot be activated - missing plans or pricing"),
            @ApiResponse(responseCode = "404", description = "Service product not found")
    })
    public ResponseEntity<GenericResponse> activateServiceProduct(
            @Parameter(description = "Service product ID") @PathVariable Long id) {
        log.info("Received request to activate service product ID: {}", id);
        GenericResponse response = serviceProductService.activateServiceProduct(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Deactivate service product",
            description = "Deactivates an active service product. Users will no longer see it.")
    public ResponseEntity<GenericResponse> deactivateServiceProduct(
            @Parameter(description = "Service product ID") @PathVariable Long id) {
        log.info("Received request to deactivate service product ID: {}", id);
        GenericResponse response = serviceProductService.deactivateServiceProduct(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PatchMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Archive service product",
            description = "Archives a service product. Archived products are no longer available.")
    public ResponseEntity<GenericResponse> archiveServiceProduct(
            @Parameter(description = "Service product ID") @PathVariable Long id) {
        log.info("Received request to archive service product ID: {}", id);
        GenericResponse response = serviceProductService.archiveServiceProduct(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping("/{id}/activation-eligibility")
    @Operation(summary = "Check activation eligibility",
            description = "Checks if a product can be activated and returns validation details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eligibility check completed")
    })
    public ResponseEntity<GenericResponse> checkActivationEligibility(
            @Parameter(description = "Service product ID") @PathVariable Long id) {
        log.info("Received request to check activation eligibility for service product ID: {}", id);
        GenericResponse response = serviceProductService.checkActivationEligibility(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Delete service product", description = "Permanently deletes a service product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Service product not found")
    })
    public ResponseEntity<GenericResponse> deleteServiceProduct(
            @Parameter(description = "Service product ID") @PathVariable Long id) {
        log.info("Received request to delete service product ID: {}", id);
        GenericResponse response = serviceProductService.deleteServiceProduct(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}