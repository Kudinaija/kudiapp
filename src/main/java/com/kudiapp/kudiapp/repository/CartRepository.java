package com.kudiapp.kudiapp.repository;

import com.kudiapp.kudiapp.enums.productService.CartStatus;
import com.kudiapp.kudiapp.models.productService.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByCartReference(String cartReference);

    Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);

    @Query("SELECT c FROM Cart c WHERE c.userId = :userId AND c.status = 'ACTIVE'")
    Optional<Cart> findActiveCartByUserId(@Param("userId") Long userId);

    List<Cart> findByUserId(Long userId);

    List<Cart> findByStatus(CartStatus status);

    @Query("SELECT c FROM Cart c WHERE c.status = 'ACTIVE' " +
           "AND c.lastActivityAt < :expiryDate")
    List<Cart> findExpiredActiveCarts(@Param("expiryDate") LocalDateTime expiryDate);

    Long countByUserId(Long userId);

    Long countByUserIdAndStatus(Long userId, CartStatus status);

    boolean existsByUserIdAndStatus(Long userId, CartStatus status);

    boolean existsByCartReference(String cartReference);

    Optional<Cart> findByPaymentReference(String paymentReference);
}