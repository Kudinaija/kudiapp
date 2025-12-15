package com.kudiapp.kudiapp.controller;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.productService.OrderRequestDto;
import com.kudiapp.kudiapp.enums.productService.OrderAction;
import com.kudiapp.kudiapp.enums.productService.OrderStatus;
import com.kudiapp.kudiapp.services.productService.OrderService;
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
 * REST Controller for managing orders
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Management", description = "Endpoints for managing service product orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(
            summary = "Create a new order",
            description = "Creates a new order for a service product and plan. Order is created in PENDING status and can be added to cart."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Order created successfully",
                    content = @Content(schema = @Schema(implementation = GenericResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Service product or plan not found")
    })
    public ResponseEntity<GenericResponse> createOrder(
            @Valid @RequestBody OrderRequestDto request) {
        
        log.info("Received request to create order for service product: {}, plan: {}", 
                request.getServiceProductId(), request.getServicePlanId());
        
        GenericResponse response = orderService.createOrder(request);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get order by ID",
            description = "Retrieves a specific order by its ID. Users can only view their own orders unless they are admin."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "403", description = "Not authorized to view this order")
    })
    public ResponseEntity<GenericResponse> getOrderById(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id) {
        
        log.info("Received request to get order by ID: {}", id);
        GenericResponse response = orderService.getOrderById(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping("/reference/{reference}")
    @Operation(
            summary = "Get order by reference",
            description = "Retrieves a specific order by its reference number"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<GenericResponse> getOrderByReference(
            @Parameter(description = "Order reference", required = true)
            @PathVariable String reference) {
        
        log.info("Received request to get order by reference: {}", reference);
        GenericResponse response = orderService.getOrderByReference(reference);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping("/my-orders")
    @Operation(
            summary = "Get current user's orders",
            description = "Retrieves all orders for the currently authenticated user with pagination"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    })
    public ResponseEntity<GenericResponse> getUserOrders(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) 
            Pageable pageable) {
        
        log.info("Received request to get user orders - page: {}, size: {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        GenericResponse response = orderService.getUserOrders(pageable);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping("/my-orders/status/{status}")
    @Operation(
            summary = "Get user orders by status",
            description = "Retrieves orders for the current user filtered by status"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    })
    public ResponseEntity<GenericResponse> getUserOrdersByStatus(
            @Parameter(description = "Order status", required = true)
            @PathVariable OrderStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) 
            Pageable pageable) {
        
        log.info("Received request to get user orders by status: {}", status);
        GenericResponse response = orderService.getUserOrdersByStatus(status, pageable);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(
            summary = "Get all orders (Admin)",
            description = "Retrieves all orders with optional filters. Admin only endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    })
    public ResponseEntity<GenericResponse> getAllOrders(
            @Parameter(description = "Filter by status") 
            @RequestParam(required = false) OrderStatus status,
            @Parameter(description = "Filter by action") 
            @RequestParam(required = false) OrderAction action,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) 
            Pageable pageable) {
        
        log.info("Admin request to get all orders - status: {}, action: {}", status, action);
        GenericResponse response = orderService.getAllOrders(status, action, pageable);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PatchMapping("/{id}/action")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(
            summary = "Update order action (Admin)",
            description = "Updates the administrative action status of an order. Admin only."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order action updated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Invalid action transition")
    })
    public ResponseEntity<GenericResponse> updateOrderAction(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "New action status", required = true)
            @RequestParam OrderAction action,
            @Parameter(description = "Admin notes (optional)")
            @RequestParam(required = false) String adminNotes) {
        
        log.info("Admin request to update order {} action to: {}", id, action);
        GenericResponse response = orderService.updateOrderAction(id, action, adminNotes);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PatchMapping("/{id}/cancel")
    @Operation(
            summary = "Cancel an order",
            description = "Cancels an order. Users can cancel their own pending orders. Admin can cancel any order."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
            @ApiResponse(responseCode = "403", description = "Not authorized to cancel this order")
    })
    public ResponseEntity<GenericResponse> cancelOrder(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id) {
        
        log.info("Received request to cancel order: {}", id);
        GenericResponse response = orderService.cancelOrder(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(
            summary = "Delete an order (Admin)",
            description = "Permanently deletes an order. Only unpaid orders can be deleted. Admin only."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Cannot delete paid orders")
    })
    public ResponseEntity<GenericResponse> deleteOrder(
            @Parameter(description = "Order ID", required = true)
            @PathVariable Long id) {
        
        log.info("Admin request to delete order: {}", id);
        GenericResponse response = orderService.deleteOrder(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(
            summary = "Get order statistics (Admin)",
            description = "Retrieves order statistics including counts by status and action. Admin only."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    public ResponseEntity<GenericResponse> getOrderStatistics() {
        log.info("Admin request to get order statistics");
        GenericResponse response = orderService.getOrderStatistics();
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}