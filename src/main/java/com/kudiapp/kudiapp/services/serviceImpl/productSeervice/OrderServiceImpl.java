package com.kudiapp.kudiapp.services.serviceImpl.productSeervice;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.productService.OrderResponseDto;
import com.kudiapp.kudiapp.dto.productService.ServiceRequestDto;
import com.kudiapp.kudiapp.enums.productService.*;
import com.kudiapp.kudiapp.exceptions.InvalidOperationException;
import com.kudiapp.kudiapp.exceptions.ResourceNotFoundException;
import com.kudiapp.kudiapp.models.User;
import com.kudiapp.kudiapp.models.productService.*;
import com.kudiapp.kudiapp.repository.OrderRepository;
import com.kudiapp.kudiapp.repository.ServiceProductPlanRepository;
import com.kudiapp.kudiapp.repository.ServiceProductRepository;
import com.kudiapp.kudiapp.services.productService.CurrencyExchangeRateService;
import com.kudiapp.kudiapp.services.productService.OrderService;
import com.kudiapp.kudiapp.utills.CredentialEncryptionUtil;
import com.kudiapp.kudiapp.utills.ReferenceGeneratorUtil;
import com.kudiapp.kudiapp.utills.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of OrderService for managing product service orders
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ServiceProductRepository serviceProductRepository;
    private final ServiceProductPlanRepository servicePlanRepository;
    private final CurrencyExchangeRateService exchangeRateService;
    private final CredentialEncryptionUtil encryptionUtil;
    private final SecurityUtil securityUtil;

    // Service fee percentage (e.g., 2% of order amount)
    private static final BigDecimal SERVICE_FEE_PERCENTAGE = new BigDecimal("0.02");

    @Override
    public GenericResponse createOrder(ServiceRequestDto request) {
        log.info("Creating order for service product ID: {} and plan ID: {}", 
                request.getServiceProductId(), request.getServicePlanId());

        // Get current user
        User currentUser = securityUtil.getCurrentLoggedInUser();
        log.debug("Order creation requested by user: {} (ID: {})", 
                currentUser.getEmail(), currentUser.getId());

        // Validate and fetch service product
        ServiceProduct serviceProduct = validateAndFetchServiceProduct(
                request.getServiceProductId());

        // Validate and fetch service plan
        ServiceProductPlan servicePlan = validateAndFetchServicePlan(
                request.getServicePlanId(), 
                serviceProduct.getId()
        );

        // Fetch product price
        ServiceProductPrice productPrice = servicePlan.getProductPrice();
        if (productPrice == null) {
            log.error("No price configured for service plan ID: {}", servicePlan.getId());
            throw new ResourceNotFoundException(
                    "Price not configured for the selected service plan");
        }

        // Get latest conversion rate
        BigDecimal conversionRate = exchangeRateService.getConversionRate(
                productPrice.getDefaultCurrency(),
                Currency.NGN  // We standardize to NGN for payments
        );
        log.debug("Current conversion rate from {} to NGN: {}", 
                productPrice.getDefaultCurrency(), conversionRate);

        // Calculate amounts
        BigDecimal amountInNGN = productPrice.getDefaultPrice()
                .multiply(conversionRate)
                .setScale(4, RoundingMode.HALF_UP);
        
        BigDecimal serviceFee = amountInNGN
                .multiply(SERVICE_FEE_PERCENTAGE)
                .setScale(4, RoundingMode.HALF_UP);

        // Create order
        Order order = buildOrder(
                currentUser,
                serviceProduct,
                servicePlan,
                productPrice,
                conversionRate,
                amountInNGN,
                serviceFee,
                request
        );

        // Encrypt credentials if provided
        if (request.getCredentialUsernameOrEmail() != null) {
            order.setCredentialUsernameOrEmail(
                    encryptionUtil.encrypt(request.getCredentialUsernameOrEmail())
            );
        }
        if (request.getCredentialPassword() != null) {
            order.setCredentialPassword(
                    encryptionUtil.encrypt(request.getCredentialPassword())
            );
        }

        // Calculate total amount
        order.calculateTotalAmount();

        // Save order
        Order savedOrder = orderRepository.save(order);
        log.info("Successfully created order with reference: {}", savedOrder.getOrderReference());

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Order created successfully")
                .httpStatus(HttpStatus.CREATED)
                .data(mapToResponseDto(savedOrder, false))
                .build();
    }

    @SneakyThrows
    @Override
    @Transactional(readOnly = true)
    public GenericResponse getOrderById(Long orderId) {
        log.info("Retrieving order with ID: {}", orderId);

        User currentUser = securityUtil.getCurrentLoggedInUser();
        Order order = findOrderById(orderId);

        // Check authorization - users can only view their own orders
        if (!order.getUserId().equals(currentUser.getId()) && 
            !hasAdminRole(currentUser)) {
            log.warn("User {} attempted to access order {} belonging to user {}", 
                    currentUser.getId(), orderId, order.getUserId());
            throw new InvalidOperationException("You are not authorized to view this order");
        }

        boolean isAdmin = hasAdminRole(currentUser);
        return GenericResponse.builder()
                .isSuccess(true)
                .message("Order retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponseDto(order, isAdmin))
                .build();
    }

    @SneakyThrows
    @Override
    @Transactional(readOnly = true)
    public GenericResponse getOrderByReference(String orderReference) {
        log.info("Retrieving order with reference: {}", orderReference);

        User currentUser = securityUtil.getCurrentLoggedInUser();
        Order order = orderRepository.findByOrderReference(orderReference)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with reference: " + orderReference));

        // Check authorization
        if (!order.getUserId().equals(currentUser.getId()) && 
            !hasAdminRole(currentUser)) {
            throw new InvalidOperationException("You are not authorized to view this order");
        }

        boolean isAdmin = hasAdminRole(currentUser);
        return GenericResponse.builder()
                .isSuccess(true)
                .message("Order retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponseDto(order, isAdmin))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GenericResponse getUserOrders(Pageable pageable) {
        log.info("Retrieving orders for current user");

        User currentUser = securityUtil.getCurrentLoggedInUser();
        Page<Order> orders = orderRepository.findByUserId(
                currentUser.getId(), 
                pageable
        );

        Page<OrderResponseDto> responseDtos = orders.map(
                order -> mapToResponseDto(order, false)
        );

        return GenericResponse.builder()
                .isSuccess(true)
                .message("User orders retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(responseDtos)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GenericResponse getUserOrdersByStatus(OrderStatus status, Pageable pageable) {
        log.info("Retrieving orders for current user with status: {}", status);

        User currentUser = securityUtil.getCurrentLoggedInUser();
        Page<Order> orders = orderRepository.findByUserIdAndStatus(
                currentUser.getId(),
                status,
                pageable
        );

        Page<OrderResponseDto> responseDtos = orders.map(
                order -> mapToResponseDto(order, false)
        );

        return GenericResponse.builder()
                .isSuccess(true)
                .message("User orders retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(responseDtos)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GenericResponse getAllOrders(
            OrderStatus status,
            OrderAction action,
            Pageable pageable) {

        log.info("Admin retrieving orders with filters - status: {}, action: {}",
                status, action);

        Page<Order> orders;

        if (status != null && action != null) {
            // Custom query needed for both filters
            orders = orderRepository.findAll(pageable);
        } else if (status != null) {
            orders = orderRepository.findByStatus(status, pageable);
        } else if (action != null) {
            orders = orderRepository.findByAction(action, pageable);
        } else {
            orders = orderRepository.findAll(pageable);
        }

        Page<OrderResponseDto> responseDtos = orders.map(
                order -> mapToResponseDto(order, true)
        );

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Orders retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(responseDtos)
                .build();
    }

    @Override
    @SneakyThrows
    public GenericResponse updateOrderAction(
            Long orderId,
            OrderAction action,
            String adminNotes) {

        log.info("Updating order {} action to: {}", orderId, action);

        Order order = findOrderById(orderId);

        // Validate action transition
        if (!isValidActionTransition(order.getAction(), action)) {
            throw new InvalidOperationException(
                    "Invalid action transition from " + order.getAction() + " to " + action);
        }

        order.setAction(action);
        if (adminNotes != null && !adminNotes.trim().isEmpty()) {
            order.setAdminNotes(adminNotes);
        }

        // Update order status based on action
        if (action == OrderAction.COMPLETED) {
            order.setStatus(OrderStatus.COMPLETED);
        } else if (action == OrderAction.REJECTED) {
            order.setStatus(OrderStatus.CANCELLED);
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("Successfully updated order {} action to: {}", orderId, action);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Order action updated successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponseDto(updatedOrder, true))
                .build();
    }

    @Override
    @SneakyThrows
    public GenericResponse cancelOrder(Long orderId) {
        log.info("Cancelling order with ID: {}", orderId);

        User currentUser = securityUtil.getCurrentLoggedInUser();
        Order order = findOrderById(orderId);

        // Check authorization
        if (!order.getUserId().equals(currentUser.getId()) &&
                !hasAdminRole(currentUser)) {
            throw new InvalidOperationException(
                    "You are not authorized to cancel this order");
        }

        // Check if order can be cancelled
        if (!order.getStatus().canBeCancelled()) {
            throw new InvalidOperationException(
                    "Order with status " + order.getStatus() + " cannot be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setAction(OrderAction.REJECTED);
        Order cancelledOrder = orderRepository.save(order);

        log.info("Successfully cancelled order: {}", order.getOrderReference());

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Order cancelled successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponseDto(cancelledOrder, hasAdminRole(currentUser)))
                .build();
    }

    @SneakyThrows
    @Override
    public GenericResponse deleteOrder(Long orderId) {
        log.info("Deleting order with ID: {}", orderId);

        Order order = findOrderById(orderId);

        // Only allow deletion of pending/cancelled orders
        if (order.getStatus().isPaid()) {
            throw new InvalidOperationException(
                    "Cannot delete paid orders. Please cancel first if applicable.");
        }

        orderRepository.delete(order);
        log.info("Successfully deleted order: {}", order.getOrderReference());

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Order deleted successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GenericResponse getOrderStatistics() {
        log.info("Retrieving order statistics");

        Map<String, Object> statistics = new HashMap<>();

        // Total orders
        statistics.put("totalOrders", orderRepository.count());

        // Orders by status
        Map<String, Long> ordersByStatus = new HashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            ordersByStatus.put(
                    status.name(),
                    orderRepository.countByStatus(status)
            );
        }
        statistics.put("ordersByStatus", ordersByStatus);

        // Orders by action
        Map<String, Long> ordersByAction = new HashMap<>();
        for (OrderAction action : OrderAction.values()) {
            ordersByAction.put(
                    action.name(),
                    orderRepository.countByAction(action)
            );
        }
        statistics.put("ordersByAction", ordersByAction);

        // Pending admin review
        List<OrderAction> pendingActions = List.of(
                OrderAction.PENDING_REVIEW,
                OrderAction.IN_PROGRESS,
                OrderAction.REQUIRES_INFO
        );
        statistics.put(
                "pendingAdminReview",
                orderRepository.countByActionIn(pendingActions)
        );

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Order statistics retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(statistics)
                .build();
    }

    // PRIVATE HELPER METHODS

    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with ID: " + orderId));
    }

    @SneakyThrows
    private ServiceProduct validateAndFetchServiceProduct(Long serviceProductId) {
        ServiceProduct product = serviceProductRepository.findById(serviceProductId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service product not found with ID: " + serviceProductId));

        if (!product.isActive()) {
            log.error("Service product {} is not active. Current status: {}",
                    serviceProductId, product.getStatus());
            throw new InvalidOperationException(
                    "Service product is not available for ordering");
        }

        return product;
    }

    @SneakyThrows
    private ServiceProductPlan validateAndFetchServicePlan(
            Long servicePlanId,
            Long serviceProductId) {

        ServiceProductPlan plan = servicePlanRepository.findById(servicePlanId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service plan not found with ID: " + servicePlanId));

        if (!plan.getServiceProduct().getId().equals(serviceProductId)) {
            log.error("Service plan {} does not belong to service product {}",
                    servicePlanId, serviceProductId);
            throw new InvalidOperationException(
                    "Selected plan does not belong to the specified service product");
        }

        if (!ServiceProductPlanStatus.ACTIVE.equals(plan.getStatus())) {
            log.error("Service plan {} is not active. Current status: {}",
                    servicePlanId, plan.getStatus());
            throw new InvalidOperationException(
                    "Selected service plan is not available");
        }

        return plan;
    }

    private Order buildOrder(
            User user,
            ServiceProduct serviceProduct,
            ServiceProductPlan servicePlan,
            ServiceProductPrice productPrice,
            BigDecimal conversionRate,
            BigDecimal amountInNGN,
            BigDecimal serviceFee,
            ServiceRequestDto request) {

        return Order.builder()
                .userId(user.getId())
                .userName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .orderReference(ReferenceGeneratorUtil.generateOrderReference())
                .serviceProductId(serviceProduct.getId())
                .serviceProductName(serviceProduct.getProductTitle().getDescription())
                .servicePlanId(servicePlan.getId())
                .servicePlanName(servicePlan.getPlanName())
                .defaultAmount(productPrice.getDefaultPrice())
                .defaultCurrency(productPrice.getDefaultCurrency())
                .amount(amountInNGN)
                .amountCurrency(Currency.NGN)
                .currencyConversionRate(conversionRate)
                .serviceFee(serviceFee)
                .status(OrderStatus.PENDING)
                .action(OrderAction.PENDING_REVIEW)
                .metadata(request.getMetadata())
                .isInCart(true)
                .build();
    }

    private OrderResponseDto mapToResponseDto(Order order, boolean includeCredentials) {
        OrderResponseDto.OrderResponseDtoBuilder builder = OrderResponseDto.builder()
                .id(order.getId())
                .orderReference(order.getOrderReference())
                .userId(order.getUserId())
                .userName(order.getUserName())
                .email(order.getEmail())
                .phoneNumber(order.getPhoneNumber())
                .serviceProductId(order.getServiceProductId())
                .serviceProductName(order.getServiceProductName())
                .servicePlanId(order.getServicePlanId())
                .servicePlanName(order.getServicePlanName())
                .defaultAmount(order.getDefaultAmount())
                .defaultCurrency(order.getDefaultCurrency())
                .amount(order.getAmount())
                .amountCurrency(order.getAmountCurrency())
                .currencyConversionRate(order.getCurrencyConversionRate())
                .serviceFee(order.getServiceFee())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .action(order.getAction())
                .metadata(order.getMetadata())
                .adminNotes(order.getAdminNotes())
                .paymentReference(order.getPaymentReference())
                .isInCart(order.getIsInCart())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt());

        // Only include decrypted credentials for admin users
        if (includeCredentials) {
            if (order.getCredentialUsernameOrEmail() != null) {
                builder.credentialUsernameOrEmail(
                        encryptionUtil.decrypt(order.getCredentialUsernameOrEmail())
                );
            }
        }

        return builder.build();
    }

    private boolean hasAdminRole(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals("ROLE_ADMIN") ||
                        role.getRoleName().equals("ROLE_SUPER_ADMIN"));
    }

    private boolean isValidActionTransition(OrderAction current, OrderAction next) {
        // Define valid transitions
        if (current == OrderAction.PENDING_REVIEW) {
            return next == OrderAction.IN_PROGRESS ||
                    next == OrderAction.REJECTED ||
                    next == OrderAction.REQUIRES_INFO;
        }
        if (current == OrderAction.IN_PROGRESS) {
            return next == OrderAction.COMPLETED ||
                    next == OrderAction.REQUIRES_INFO;
        }
        if (current == OrderAction.REQUIRES_INFO) {
            return next == OrderAction.IN_PROGRESS ||
                    next == OrderAction.REJECTED;
        }
        // COMPLETED and REJECTED are terminal states
        return false;
    }
}