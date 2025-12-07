package com.kudiapp.kudiapp.controller;

import com.kudiapp.kudiapp.dto.GenericResponse;
import com.kudiapp.kudiapp.dto.productService.CurrencyExchangeRateRequest;
import com.kudiapp.kudiapp.enums.productService.Currency;
import com.kudiapp.kudiapp.services.productService.CurrencyExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/exchange-rates")
@RequiredArgsConstructor
@Tag(name = "Currency Exchange Rate Management", description = "APIs for managing currency exchange rates")
public class CurrencyExchangeRateController {

    private final CurrencyExchangeRateService exchangeRateService;

    @PostMapping
    @Operation(summary = "Create exchange rate", description = "Creates a new currency exchange rate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Exchange rate created successfully",
                    content = @Content(schema = @Schema(implementation = GenericResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<GenericResponse> createExchangeRate(
            @Valid @RequestBody CurrencyExchangeRateRequest request) {
        GenericResponse response = exchangeRateService.createExchangeRate(request);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update exchange rate", description = "Updates an existing exchange rate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exchange rate updated successfully"),
            @ApiResponse(responseCode = "404", description = "Exchange rate not found")
    })
    public ResponseEntity<GenericResponse> updateExchangeRate(
            @Parameter(description = "Exchange rate ID") @PathVariable Long id,
            @Valid @RequestBody CurrencyExchangeRateRequest request) {
        GenericResponse response = exchangeRateService.updateExchangeRate(id, request);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get exchange rate by ID", description = "Retrieves a specific exchange rate by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exchange rate retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Exchange rate not found")
    })
    public ResponseEntity<GenericResponse> getExchangeRateById(
            @Parameter(description = "Exchange rate ID") @PathVariable Long id) {
        GenericResponse response = exchangeRateService.getExchangeRateById(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest effective rate", 
               description = "Retrieves the latest effective exchange rate for a currency pair")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Latest rate retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No effective rate found for the currency pair")
    })
    public ResponseEntity<GenericResponse> getLatestEffectiveRate(
            @Parameter(description = "Source currency") @RequestParam Currency fromCurrency,
            @Parameter(description = "Target currency") @RequestParam Currency toCurrency) {
        GenericResponse response = exchangeRateService.getLatestEffectiveRate(fromCurrency, toCurrency);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @GetMapping("/effective")
    @Operation(summary = "Get all effective rates", description = "Retrieves all currently effective exchange rates")
    public ResponseEntity<GenericResponse> getAllCurrentlyEffectiveRates() {
        GenericResponse response = exchangeRateService.getAllCurrentlyEffectiveRates();
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete exchange rate", description = "Permanently deletes an exchange rate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exchange rate deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Exchange rate not found")
    })
    public ResponseEntity<GenericResponse> deleteExchangeRate(
            @Parameter(description = "Exchange rate ID") @PathVariable Long id) {
        GenericResponse response = exchangeRateService.deleteExchangeRate(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}