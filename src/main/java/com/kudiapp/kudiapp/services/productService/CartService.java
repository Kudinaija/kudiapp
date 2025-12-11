package com.kudiapp.kudiapp.services.productService;

import com.kudiapp.kudiapp.dto.GenericResponse;

/**
 * Service interface for managing shopping carts
 */
public interface CartService {

    /**
     * Get or create active cart for current user
     * 
     * @return GenericResponse containing cart details
     */
    GenericResponse getOrCreateCart();

    /**
     * Get active cart for current user
     * 
     * @return GenericResponse containing cart details
     */
    GenericResponse getActiveCart();

    /**
     * Add an order to the cart
     * 
     * @param orderId The order ID to add
     * @return GenericResponse with updated cart
     */
    GenericResponse addOrderToCart(Long orderId);

    /**
     * Remove an order from the cart
     * 
     * @param orderId The order ID to remove
     * @return GenericResponse with updated cart
     */
    GenericResponse removeOrderFromCart(Long orderId);

    /**
     * Clear all items from cart
     * 
     * @return GenericResponse with empty cart
     */
    GenericResponse clearCart();

    /**
     * Get cart summary with live exchange rates
     * 
     * @return GenericResponse containing detailed cart summary
     */
    GenericResponse getCartSummary();

    /**
     * Proceed to checkout - prepares cart for payment
     * 
     * @return GenericResponse containing checkout details
     */
    GenericResponse proceedToCheckout();

    /**
     * Get all user carts (including historical)
     * 
     * @return GenericResponse containing all user carts
     */
    GenericResponse getUserCarts();
}