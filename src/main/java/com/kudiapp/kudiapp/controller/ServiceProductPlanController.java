package com.kudiapp.kudiapp.controller;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.productService.ServiceProductPlanRequest;
import com.kudiapp.kudiapp.enums.productService.ServiceProductPlanStatus;
import com.kudiapp.kudiapp.services.productService.ServiceProductPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for managing service product plans
 * 
 * @author KudiApp Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/product-plans")
@RequiredArgsConstructor
@Tag(name = "Product Plan Management", description = "Endpoints for managing service product plans and pricing tiers")
public class ServiceProductPlanController {

    private final ServiceProductPlanService productPlanService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(
            summary = "Create a new product plan",
            description = "Creates a new pricing plan for a service product. Plan starts in ACTIVE status by default."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Product plan created successfully",
                    content = @Content(schema = @Schema(implementation = GenericResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Service product not found"),
            @ApiResponse(responseCode = "409", description = "Plan with same name already exists for this product")
    })
    public ResponseEntity<GenericResponse> createProductPlan(
            @Valid @RequestBody ServiceProductPlanRequest request) {
        GenericResponse response = productPlanService.createProductPlan(request);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(
            summary = "Update a product plan",
            description = "Updates an existing product plan. Cannot change the associated service product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product plan updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Product plan not found"),
            @ApiResponse(responseCode = "409", description = "Plan name already exists for this product")
    })
    public ResponseEntity<GenericResponse> updateProductPlan(
            @Parameter(description = "Product plan ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ServiceProductPlanRequest request) {
        GenericResponse response = productPlanService.updateProductPlan(id, request);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get product plan by ID",
            description = "Retrieves detailed information about a specific product plan including its pricing"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product plan retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product plan not found")
    })
    public ResponseEntity<GenericResponse> getProductPlanById(
            @Parameter(description = "Product plan ID", required = true)
            @PathVariable Long id) {
        GenericResponse response = productPlanService.getProductPlanById(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping("/product/{serviceProductId}")
    @Operation(
            summary = "Get all plans for a service product",
            description = "Retrieves all pricing plans for a specific service product"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product plans retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Service product not found")
    })
    public ResponseEntity<GenericResponse> getProductPlansByServiceProduct(
            @Parameter(description = "Service product ID", required = true)
            @PathVariable Long serviceProductId,
            @RequestParam(required = false) ServiceProductPlanStatus status) {
        GenericResponse response = productPlanService.getProductPlansByServiceProduct(
                serviceProductId, 
                status
        );
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping("/product/{serviceProductId}/active")
    @Operation(
            summary = "Get active plans for a service product",
            description = "Retrieves only ACTIVE pricing plans for a service product (user-facing endpoint)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active plans retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Service product not found or no active plans")
    })
    public ResponseEntity<GenericResponse> getActiveProductPlans(
            @Parameter(description = "Service product ID", required = true)
            @PathVariable Long serviceProductId) {
        GenericResponse response = productPlanService.getActiveProductPlans(serviceProductId);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(
            summary = "Get all product plans (Admin)",
            description = "Retrieves all product plans with optional filters and pagination"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product plans retrieved successfully")
    })
    public ResponseEntity<GenericResponse> getAllProductPlans(
            @Parameter(description = "Filter by status")
            @RequestParam(required = false) ServiceProductPlanStatus status,
            @Parameter(description = "Filter by featured plans only")
            @RequestParam(required = false) Boolean isFeatured,
            @PageableDefault(size = 20, sort = "displayOrder", direction = Sort.Direction.ASC)
            Pageable pageable) {
        GenericResponse response = productPlanService.getAllProductPlans(
                status, 
                isFeatured, 
                pageable
        );
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(
            summary = "Activate a product plan",
            description = "Changes plan status to ACTIVE. Only active plans can be ordered by users."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product plan activated successfully"),
            @ApiResponse(responseCode = "404", description = "Product plan not found")
    })
    public ResponseEntity<GenericResponse> activateProductPlan(
            @Parameter(description = "Product plan ID", required = true)
            @PathVariable Long id) {
        GenericResponse response = productPlanService.activateProductPlan(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(
            summary = "Deactivate a product plan",
            description = "Changes plan status to DEACTIVATED. Users cannot order deactivated plans."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product plan deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Product plan not found")
    })
    public ResponseEntity<GenericResponse> deactivateProductPlan(
            @Parameter(description = "Product plan ID", required = true)
            @PathVariable Long id) {
        GenericResponse response = productPlanService.deactivateProductPlan(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PatchMapping("/{id}/suspend")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(
            summary = "Suspend a product plan",
            description = "Temporarily suspends a plan. Different from deactivation - indicates temporary unavailability."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product plan suspended successfully"),
            @ApiResponse(responseCode = "404", description = "Product plan not found")
    })
    public ResponseEntity<GenericResponse> suspendProductPlan(
            @Parameter(description = "Product plan ID", required = true)
            @PathVariable Long id) {
        GenericResponse response = productPlanService.suspendProductPlan(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PatchMapping("/{id}/feature")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(
            summary = "Toggle featured status",
            description = "Marks or unmarks a plan as featured. Featured plans are highlighted in UI."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Featured status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product plan not found")
    })
    public ResponseEntity<GenericResponse> toggleFeaturedStatus(
            @Parameter(description = "Product plan ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Set as featured")
            @RequestParam Boolean featured) {
        GenericResponse response = productPlanService.toggleFeaturedStatus(id, featured);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PatchMapping("/{id}/display-order")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(
            summary = "Update display order",
            description = "Updates the display order of a plan. Lower numbers appear first."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Display order updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product plan not found")
    })
    public ResponseEntity<GenericResponse> updateDisplayOrder(
            @Parameter(description = "Product plan ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "New display order value")
            @RequestParam Integer displayOrder) {
        GenericResponse response = productPlanService.updateDisplayOrder(id, displayOrder);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(
            summary = "Delete a product plan",
            description = "Permanently deletes a product plan. Cannot delete plans with existing orders."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product plan deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Cannot delete - Plan has existing orders"),
            @ApiResponse(responseCode = "404", description = "Product plan not found")
    })
    public ResponseEntity<GenericResponse> deleteProductPlan(
            @Parameter(description = "Product plan ID", required = true)
            @PathVariable Long id) {
        GenericResponse response = productPlanService.deleteProductPlan(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping("/featured")
    @Operation(
            summary = "Get all featured plans",
            description = "Retrieves all currently featured and active plans across all products"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Featured plans retrieved successfully")
    })
    public ResponseEntity<GenericResponse> getFeaturedPlans() {
        GenericResponse response = productPlanService.getFeaturedPlans();
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}