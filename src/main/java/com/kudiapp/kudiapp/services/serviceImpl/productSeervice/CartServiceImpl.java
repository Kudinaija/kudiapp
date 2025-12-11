package com.kudiapp.kudiapp.services.serviceImpl.productSeervice;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.productService.CartResponseDto;
import com.kudiapp.kudiapp.enums.productService.CartStatus;
import com.kudiapp.kudiapp.enums.productService.Currency;
import com.kudiapp.kudiapp.enums.productService.OrderStatus;
import com.kudiapp.kudiapp.exceptions.InvalidOperationException;
import com.kudiapp.kudiapp.exceptions.ResourceNotFoundException;
import com.kudiapp.kudiapp.models.User;
import com.kudiapp.kudiapp.models.productService.Cart;
import com.kudiapp.kudiapp.models.productService.Order;
import com.kudiapp.kudiapp.repository.CartRepository;
import com.kudiapp.kudiapp.repository.OrderRepository;
import com.kudiapp.kudiapp.services.productService.CartService;
import com.kudiapp.kudiapp.services.productService.CurrencyExchangeRateService;
import com.kudiapp.kudiapp.utills.ReferenceGeneratorUtil;
import com.kudiapp.kudiapp.utills.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of CartService for managing shopping carts
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CurrencyExchangeRateService exchangeRateService;
    private final SecurityUtil securityUtil;

    @Override
    public GenericResponse getOrCreateCart() {
        log.info("Getting or creating cart for current user");

        User currentUser = securityUtil.getCurrentLoggedInUser();
        
        Cart cart = cartRepository.findActiveCartByUserId(currentUser.getId())
                .orElseGet(() -> {
                    log.info("Creating new cart for user: {}", currentUser.getId());
                    return createNewCart(currentUser);
                });

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Cart retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponseDto(cart))
                .build();
    }

    @Override
    public GenericResponse getActiveCart() {
        log.info("Retrieving active cart for current user");

        User currentUser = securityUtil.getCurrentLoggedInUser();
        
        Cart cart = cartRepository.findActiveCartByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active cart found. Please add items to create a cart."));

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Active cart retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponseDto(cart))
                .build();
    }

    @Override
    @SneakyThrows
    public GenericResponse addOrderToCart(Long orderId) {
        log.info("Adding order {} to cart", orderId);

        User currentUser = securityUtil.getCurrentLoggedInUser();
        
        // Get or create cart
        Cart cart = cartRepository.findActiveCartByUserId(currentUser.getId())
                .orElseGet(() -> createNewCart(currentUser));

        // Get order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with ID: " + orderId));

        // Validate order belongs to user
        if (!order.getUserId().equals(currentUser.getId())) {
            throw new InvalidOperationException(
                    "You are not authorized to add this order to cart");
        }

        // Validate order can be added to cart
        if (!OrderStatus.PENDING.equals(order.getStatus())) {
            throw new InvalidOperationException(
                    "Only pending orders can be added to cart");
        }

        if (!order.getIsInCart()) {
            // Add order to cart
            cart.addOrder(order);
            order.setIsInCart(true);
            cartRepository.save(cart);
            orderRepository.save(order);
            
            log.info("Successfully added order {} to cart {}", orderId, cart.getId());
        }

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Order added to cart successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponseDto(cart))
                .build();
    }

    @Override
    @SneakyThrows
    public GenericResponse removeOrderFromCart(Long orderId) {
        log.info("Removing order {} from cart", orderId);

        User currentUser = securityUtil.getCurrentLoggedInUser();
        
        Cart cart = cartRepository.findActiveCartByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No active cart found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with ID: " + orderId));

        // Validate order belongs to user
        if (!order.getUserId().equals(currentUser.getId())) {
            throw new InvalidOperationException(
                    "You are not authorized to remove this order");
        }

        // Remove order from cart
        cart.removeOrder(order);
        order.setIsInCart(false);
        cartRepository.save(cart);
        orderRepository.save(order);

        log.info("Successfully removed order {} from cart", orderId);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Order removed from cart successfully")
                .httpStatus(HttpStatus.OK)
                .data(mapToResponseDto(cart))
                .build();
    }

    @Override
    public GenericResponse clearCart() {
        log.info("Clearing cart for current user");

        User currentUser = securityUtil.getCurrentLoggedInUser();
        
        Cart cart = cartRepository.findActiveCartByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No active cart found"));

        // Clear all orders
        cart.getOrders().forEach(order -> order.setIsInCart(false));
        cart.clearOrders();
        cartRepository.save(cart);

        log.info("Successfully cleared cart for user: {}", currentUser.getId());

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Cart cleared successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GenericResponse getCartSummary() {
        log.info("Retrieving cart summary with live exchange rates");

        User currentUser = securityUtil.getCurrentLoggedInUser();
        
        Cart cart = cartRepository.findActiveCartByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No active cart found"));

        // Get live exchange rates
        Map<String, BigDecimal> liveRates = fetchLiveExchangeRates();

        CartResponseDto responseDto = mapToResponseDto(cart);
        responseDto.setLiveExchangeRates(liveRates);

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Cart summary retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(responseDto)
                .build();
    }

    @Override
    @SneakyThrows
    public GenericResponse proceedToCheckout() {
        log.info("Processing checkout for current user");

        User currentUser = securityUtil.getCurrentLoggedInUser();
        
        Cart cart = cartRepository.findActiveCartByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No active cart found"));

        // Validate cart is not empty
        if (cart.isEmpty()) {
            throw new InvalidOperationException("Cannot checkout with empty cart");
        }

        // Validate all orders are in pending status
        boolean hasNonPendingOrders = cart.getOrders().stream()
                .anyMatch(order -> !OrderStatus.PENDING.equals(order.getStatus()));

        if (hasNonPendingOrders) {
            throw new InvalidOperationException(
                    "Cart contains orders that are not in pending status");
        }

        // Generate payment reference
        String paymentReference = ReferenceGeneratorUtil.generatePaymentReference();
        
        // Update cart status
        cart.checkout(paymentReference);
        cartRepository.save(cart);

        log.info("Successfully prepared cart for checkout. Payment reference: {}", 
                paymentReference);

        Map<String, Object> checkoutData = new HashMap<>();
        checkoutData.put("cartReference", cart.getCartReference());
        checkoutData.put("paymentReference", paymentReference);
        checkoutData.put("totalAmount", cart.getTotalAmount());
        checkoutData.put("currency", cart.getCurrency());
        checkoutData.put("itemCount", cart.getItemCount());

        return GenericResponse.builder()
                .isSuccess(true)
                .message("Cart prepared for checkout successfully")
                .httpStatus(HttpStatus.OK)
                .data(checkoutData)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public GenericResponse getUserCarts() {
        log.info("Retrieving all carts for current user");

        User currentUser = securityUtil.getCurrentLoggedInUser();
        
        List<Cart> carts = cartRepository.findByUserId(currentUser.getId());
        
        List<CartResponseDto> responseDtos = carts.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return GenericResponse.builder()
                .isSuccess(true)
                .message("User carts retrieved successfully")
                .httpStatus(HttpStatus.OK)
                .data(responseDtos)
                .build();
    }

    // PRIVATE HELPER METHODS

    private Cart createNewCart(User user) {
        Cart cart = Cart.builder()
                .userId(user.getId())
                .cartReference(ReferenceGeneratorUtil.generateCartReference())
                .status(CartStatus.ACTIVE)
                .currency(Currency.NGN)
                .lastActivityAt(LocalDateTime.now())
                .build();

        return cartRepository.save(cart);
    }

    private Map<String, BigDecimal> fetchLiveExchangeRates() {
        Map<String, BigDecimal> rates = new HashMap<>();
        
        try {
            rates.put("USD_TO_NGN", 
                    exchangeRateService.getConversionRate(Currency.USD, Currency.NGN));
            rates.put("EUR_TO_NGN", 
                    exchangeRateService.getConversionRate(Currency.EUR, Currency.NGN));
        } catch (Exception e) {
            log.warn("Failed to fetch live exchange rates: {}", e.getMessage());
        }
        
        return rates;
    }

    private CartResponseDto mapToResponseDto(Cart cart) {
        List<CartResponseDto.OrderSummaryDto> orderSummaries = cart.getOrders().stream()
                .map(order -> CartResponseDto.OrderSummaryDto.builder()
                        .orderId(order.getId())
                        .orderReference(order.getOrderReference())
                        .serviceProductName(order.getServiceProductName())
                        .servicePlanName(order.getServicePlanName())
                        .amount(order.getAmount())
                        .serviceFee(order.getServiceFee())
                        .totalAmount(order.getTotalAmount())
                        .currency(order.getAmountCurrency())
                        .build())
                .collect(Collectors.toList());

        return CartResponseDto.builder()
                .id(cart.getId())
                .cartReference(cart.getCartReference())
                .userId(cart.getUserId())
                .status(cart.getStatus())
                .itemCount(cart.getItemCount())
                .orderSummaries(orderSummaries)
                .subtotal(cart.getSubtotal())
                .totalServiceFee(cart.getTotalServiceFee())
                .totalAmount(cart.getTotalAmount())
                .currency(cart.getCurrency())
                .lastActivityAt(cart.getLastActivityAt())
                .checkedOutAt(cart.getCheckedOutAt())
                .paymentReference(cart.getPaymentReference())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }
}