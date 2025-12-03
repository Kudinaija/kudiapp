package com.kudiapp.kudiapp.dto.productService;

import com.kudiapp.kudiapp.enums.productService.Category;
import com.kudiapp.kudiapp.enums.productService.PRODUCT_TITLE;
import com.kudiapp.kudiapp.enums.productService.ServiceProductStatus;
import com.kudiapp.kudiapp.enums.productService.UrgencyType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProductCreationRequest {

    @NotNull(message = "Category is required")
    private Category category;

    @Size(max = 5000, message = "Category items must not exceed 5000 characters")
    private String categoryItems;

    @NotNull(message = "Product title is required")
    private PRODUCT_TITLE productTitle;

    @Size(max = 10000, message = "Product description must not exceed 10000 characters")
    private String productDescription;

    private UrgencyType urgencyType;

    private ServiceProductStatus status;

    private Map<String, Object> metadata;
}
