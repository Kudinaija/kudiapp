package com.kudiapp.kudiapp.services.productService;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.productService.OrderRequestDto;
import com.kudiapp.kudiapp.enums.productService.OrderAction;
import com.kudiapp.kudiapp.enums.productService.OrderStatus;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing orders
 */
public interface OrderService {

    /**
     * Create a new order from a service request
     * 
     * @param request The service request DTO
     * @return GenericResponse containing the created order
     */
    GenericResponse createOrder(OrderRequestDto request);

    /**
     * Get order by ID
     * 
     * @param orderId The order ID
     * @return GenericResponse containing order details
     */
    GenericResponse getOrderById(Long orderId);

    /**
     * Get order by reference number
     * 
     * @param orderReference The order reference
     * @return GenericResponse containing order details
     */
    GenericResponse getOrderByReference(String orderReference);

    /**
     * Get all orders for the current user
     * 
     * @param pageable Pagination parameters
     * @return GenericResponse containing paginated orders
     */
    GenericResponse getUserOrders(Pageable pageable);

    /**
     * Get user orders filtered by status
     * 
     * @param status The order status
     * @param pageable Pagination parameters
     * @return GenericResponse containing filtered orders
     */
    GenericResponse getUserOrdersByStatus(OrderStatus status, Pageable pageable);

    /**
     * Get all orders (Admin)
     * 
     * @param status Optional status filter
     * @param action Optional action filter
     * @param pageable Pagination parameters
     * @return GenericResponse containing paginated orders
     */
    GenericResponse getAllOrders(
            OrderStatus status, 
            OrderAction action, 
            Pageable pageable
    );

    /**
     * Update order admin action status
     * 
     * @param orderId The order ID
     * @param action The new action status
     * @param adminNotes Optional admin notes
     * @return GenericResponse with update confirmation
     */
    GenericResponse updateOrderAction(
            Long orderId, 
            OrderAction action, 
            String adminNotes
    );

    /**
     * Cancel an order
     * 
     * @param orderId The order ID
     * @return GenericResponse with cancellation confirmation
     */
    GenericResponse cancelOrder(Long orderId);

    /**
     * Delete an order (Admin only)
     * 
     * @param orderId The order ID
     * @return GenericResponse with deletion confirmation
     */
    GenericResponse deleteOrder(Long orderId);

    /**
     * Get order statistics
     * 
     * @return GenericResponse containing order statistics
     */
    GenericResponse getOrderStatistics();
}