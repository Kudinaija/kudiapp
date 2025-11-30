package com.kudiapp.kudiapp.controller;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.request.SericeProduct.ServiceProductCreationRequest;
import com.kudiapp.kudiapp.services.ServiceProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/service-products")
@RequiredArgsConstructor
@Slf4j
public class ServiceProductAdminController {

    private final ServiceProductService service;


    @Operation(
            summary = "Create a new service product",
            description = "This endpoint allows an admin to create a new ServiceProduct with metadata.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Product created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<GenericResponse> createServiceProduct(@RequestBody ServiceProductCreationRequest request) {
        return ResponseEntity.ok(service.createServiceProduct(request));
    }

    @Operation(
            summary = "Update a service product",
            description = "Admins can update any ServiceProduct field including metadata.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid update request"),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            }
    )

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse> updateServiceProduct(
            @PathVariable Long id,
            @RequestBody ServiceProductCreationRequest request
    ) {
        return ResponseEntity.ok(service.updateServiceProduct(id, request));
    }

    @Operation(
            summary = "Get a product by ID",
            description = "Returns a ServiceProduct's details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product found"),
                    @ApiResponse(responseCode = "404", description = "Product not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse> getServiceProduct(@PathVariable Long id) {
        return ResponseEntity.ok(service.getServiceProductById(id));
    }

    @Operation(
            summary = "Delete a service product",
            description = "Deletes permanently from the system. Admin only.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            }
    )

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse> deleteServiceProduct(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteServiceProduct(id));
    }

    @Operation(
            summary = "Fetch all service products with filters",
            description = "Admins can filter by category, title or urgency. Pagination supported.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Products fetched successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<GenericResponse> findAllServiceProduct(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String urgency,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        GenericResponse response = service.findAllServiceProduct(category, title, urgency, PageRequest.of(page, size));
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
