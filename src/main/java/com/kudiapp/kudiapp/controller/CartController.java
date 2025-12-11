package com.kudiapp.kudiapp.controller;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.services.productService.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

/**
 * REST Controller for managing shopping carts
 */
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Cart Management", description = "Endpoints for managing shopping carts")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(
            summary = "Get or create cart",
            description = "Retrieves the active cart for the current user or creates a new one if none exists"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cart retrieved or created successfully",
                    content = @Content(schema = @Schema(implementation = GenericResponse.class))
            )
    })
    public ResponseEntity<GenericResponse> getOrCreateCart() {
        GenericResponse response = cartService.getOrCreateCart();
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping("/active")
    @Operation(
            summary = "Get active cart",
            description = "Retrieves the active cart for the current user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active cart retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No active cart found")
    })
    public ResponseEntity<GenericResponse> getActiveCart() {
        GenericResponse response = cartService.getActiveCart();
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PostMapping("/add-order/{orderId}")
    @Operation(
            summary = "Add order to cart",
            description = "Adds a pending order to the user's active cart"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order added to cart successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Order cannot be added to cart")
    })
    public ResponseEntity<GenericResponse> addOrderToCart(
            @Parameter(description = "Order ID to add", required = true)
            @PathVariable Long orderId) {
        GenericResponse response = cartService.addOrderToCart(orderId);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @DeleteMapping("/remove-order/{orderId}")
    @Operation(
            summary = "Remove order from cart",
            description = "Removes an order from the user's active cart"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order removed from cart successfully"),
            @ApiResponse(responseCode = "404", description = "Cart or order not found")
    })
    public ResponseEntity<GenericResponse> removeOrderFromCart(
            @Parameter(description = "Order ID to remove", required = true)
            @PathVariable Long orderId) {
        GenericResponse response = cartService.removeOrderFromCart(orderId);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @DeleteMapping("/clear")
    @Operation(
            summary = "Clear cart",
            description = "Removes all orders from the user's active cart"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart cleared successfully"),
            @ApiResponse(responseCode = "404", description = "No active cart found")
    })
    public ResponseEntity<GenericResponse> clearCart() {
        GenericResponse response = cartService.clearCart();
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping("/summary")
    @Operation(
            summary = "Get cart summary",
            description = "Retrieves detailed cart summary with live exchange rates"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart summary retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No active cart found")
    })
    public ResponseEntity<GenericResponse> getCartSummary() {
        GenericResponse response = cartService.getCartSummary();
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PostMapping("/checkout")
    @Operation(
            summary = "Proceed to checkout",
            description = "Prepares the cart for payment by generating a payment reference"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checkout prepared successfully"),
            @ApiResponse(responseCode = "400", description = "Cart is empty or invalid"),
            @ApiResponse(responseCode = "404", description = "No active cart found")
    })
    public ResponseEntity<GenericResponse> proceedToCheckout() {
        GenericResponse response = cartService.proceedToCheckout();
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping("/history")
    @Operation(
            summary = "Get user cart history",
            description = "Retrieves all carts (including historical) for the current user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart history retrieved successfully")
    })
    public ResponseEntity<GenericResponse> getUserCarts() {
        GenericResponse response = cartService.getUserCarts();
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}