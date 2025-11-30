package com.kudiapp.kudiapp.dto.request.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyResponseDto {

    @JsonProperty("status")
    private boolean status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private Data data;

    @lombok.Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {

        @JsonProperty("id")
        private Long id;

        @JsonProperty("domain")
        private String domain;

        @JsonProperty("status")
        private String status;

        @JsonProperty("reference")
        private String reference;

        @JsonProperty("amount")
        private Long amount;

        @JsonProperty("message")
        private String message;

        @JsonProperty("gateway_response")
        private String gatewayResponse;

        @JsonProperty("paid_at")
        private String paidAt;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("channel")
        private String channel;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("ip_address")
        private String ipAddress;

        @JsonProperty("metadata")
        private Map<String, Object> metadata;

        @JsonProperty("log")
        private Object log;

        @JsonProperty("fees")
        private BigDecimal fees;

        @JsonProperty("fees_split")
        private Object feesSplit;

        @JsonProperty("authorization")
        private Authorization authorization;

        @JsonProperty("customer")
        private Customer customer;

        @JsonProperty("plan")
        private Object plan;

        @JsonProperty("split")
        private Object split;

        @JsonProperty("order_id")
        private String orderId;

        @JsonProperty("paidAt")
        private Instant paidAtInstant;

        @JsonProperty("createdAt")
        private Instant createdAtInstant;

        @JsonProperty("requested_amount")
        private BigDecimal requestedAmount;

        @JsonProperty("pos_transaction_data")
        private Object posTransactionData;

        @JsonProperty("source")
        private Object source;

        @JsonProperty("fees_breakdown")
        private Object feesBreakdown;

        @JsonProperty("transaction_date")
        private String transactionDate;

        @JsonProperty("plan_object")
        private Object planObject;

        @JsonProperty("subaccount")
        private Object subaccount;
    }

    @lombok.Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Authorization {

        @JsonProperty("authorization_code")
        private String authorizationCode;

        @JsonProperty("bin")
        private String bin;

        @JsonProperty("last4")
        private String last4;

        @JsonProperty("exp_month")
        private String expMonth;

        @JsonProperty("exp_year")
        private String expYear;

        @JsonProperty("channel")
        private String channel;

        @JsonProperty("card_type")
        private String cardType;

        @JsonProperty("bank")
        private String bank;

        @JsonProperty("country_code")
        private String countryCode;

        @JsonProperty("brand")
        private String brand;

        @JsonProperty("reusable")
        private boolean reusable;

        @JsonProperty("signature")
        private String signature;

        @JsonProperty("account_name")
        private String accountName;
    }

    @lombok.Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Customer {

        @JsonProperty("id")
        private Long id;

        @JsonProperty("first_name")
        private String firstName;

        @JsonProperty("last_name")
        private String lastName;

        @JsonProperty("email")
        private String email;

        @JsonProperty("customer_code")
        private String customerCode;

        @JsonProperty("phone")
        private String phone;

        @JsonProperty("metadata")
        private Map<String, Object> metadata;

        @JsonProperty("risk_action")
        private String riskAction;

        @JsonProperty("international_format_phone")
        private String internationalFormatPhone;
    }
}