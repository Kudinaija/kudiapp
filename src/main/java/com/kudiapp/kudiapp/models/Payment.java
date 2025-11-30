package com.kudiapp.kudiapp.models;

import com.kudiapp.kudiapp.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference", nullable = false, unique = true)
    private String reference;

    @Column(name = "booking_reference", nullable = false)
    private String productReference;

    @Column(name = "amount", precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "gateway_response")
    private String gatewayResponse;

    @Column(name = "channel")
    private String channel;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String email;

    private String authorizationUrl;
    private String accessCode;
    private Instant createdAt = Instant.now();
    private String metadataJson;
}