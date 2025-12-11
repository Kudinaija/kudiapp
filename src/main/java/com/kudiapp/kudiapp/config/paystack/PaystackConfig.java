package com.kudiapp.kudiapp.config.paystack;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class PaystackConfig {

    @Value("${paystack.secret-key}")
    private String secretKey;

    @Value("${paystack.public-key}")
    private String publicKey;

    @Value("${paystack.base-url:https://api.paystack.co}")
    private String baseUrl;

    @Value("${paystack.webhook-secret}")
    private String webhookSecret;

    @Value("${paystack.callback-url}")
    private String callbackUrl;
}