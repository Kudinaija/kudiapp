package com.kudiapp.kudiapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends 
        JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    Optional<Order> findByOrderReference(String orderReference);

    List<Order> findByUserId(Long userId);

    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    List<Order> findByUserIdAndIsInCart(Long userId, Boolean isInCart);

    @Query("SELECT o FROM Order o WHERE o.userId = :userId " +
           "AND o.isInCart = true AND o.cart.status = 'ACTIVE'")
    List<Order> findActiveCartOrdersByUserId(@Param("userId") Long userId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByAction(OrderAction action);

    @Query("SELECT o FROM Order o WHERE o.action IN :actions ORDER BY o.createdAt DESC")
    List<Order> findByActionIn(@Param("actions") List<OrderAction> actions);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Page<Order> findByAction(OrderAction action, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.userId = :userId " +
           "AND o.status = :status ORDER BY o.createdAt DESC")
    Page<Order> findByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") OrderStatus status,
            Pageable pageable
    );

    Long countByUserId(Long userId);

    Long countByUserIdAndStatus(Long userId, OrderStatus status);

    Long countByStatus(OrderStatus status);

    Long countByAction(OrderAction action);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.action IN :actions")
    Long countByActionIn(@Param("actions") List<OrderAction> actions);

    boolean existsByOrderReference(String orderReference);

    Optional<Order> findByPaymentReference(String paymentReference);

    @Query("SELECT o FROM Order o WHERE o.cart.id = :cartId")
    List<Order> findByCartId(@Param("cartId") Long cartId);

    /**
     * Count orders by service plan ID
     * Used to check if a plan can be deleted
     */
    Long countByServicePlanId(Long servicePlanId);

    /**
     * Find orders by service plan ID
     */
    List<Order> findByServicePlanId(Long servicePlanId);
}