package com.kudiapp.kudiapp.config.paystack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kudiapp.kudiapp.exceptions.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * HTTP Client for Paystack API using OkHttp
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaystackHttpClient {

    private final PaystackConfig paystackConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    /**
     * Make a POST request to Paystack API
     *
     * @param path     API endpoint path (e.g., "/transaction/initialize")
     * @param body     Request body object
     * @param responseType Expected response class
     * @return Response object
     */
    public <T> T post(String path, Object body, Class<T> responseType) {
        try {
            String url = paystackConfig.getBaseUrl() + path;
            String jsonBody = objectMapper.writeValueAsString(body);

            log.debug("POST request to Paystack: {} with body: {}", url, jsonBody);

            RequestBody requestBody = RequestBody.create(
                    jsonBody,
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Authorization", "Bearer " + paystackConfig.getSecretKey())
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";

                log.debug("Paystack response status: {}, body: {}", response.code(), responseBody);

                if (!response.isSuccessful()) {
                    log.error("Paystack API error: {} - {}", response.code(), responseBody);
                    throw new PaymentException("Paystack API error: " + response.code() + " - " + responseBody);
                }

                return objectMapper.readValue(responseBody, responseType);
            }
        } catch (IOException e) {
            log.error("Error calling Paystack API: {}", e.getMessage(), e);
            throw new PaymentException("Failed to communicate with Paystack: " + e.getMessage());
        }
    }

    /**
     * Make a GET request to Paystack API
     *
     * @param path     API endpoint path
     * @param responseType Expected response class
     * @return Response object
     */
    public <T> T get(String path, Class<T> responseType) {
        try {
            String url = paystackConfig.getBaseUrl() + path;
            log.debug("GET request to Paystack: {}", url);

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Authorization", "Bearer " + paystackConfig.getSecretKey())
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";

                log.debug("Paystack response status: {}, body: {}", response.code(), responseBody);

                if (!response.isSuccessful()) {
                    log.error("Paystack API error: {} - {}", response.code(), responseBody);
                    throw new PaymentException("Paystack API error: " + response.code() + " - " + responseBody);
                }

                return objectMapper.readValue(responseBody, responseType);
            }
        } catch (IOException e) {
            log.error("Error calling Paystack API: {}", e.getMessage(), e);
            throw new PaymentException("Failed to communicate with Paystack: " + e.getMessage());
        }
    }
}