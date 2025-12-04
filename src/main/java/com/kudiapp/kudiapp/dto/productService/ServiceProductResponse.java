package com.kudiapp.kudiapp.dto.productService;

import com.kudiapp.kudiapp.enums.productService.Category;
import com.kudiapp.kudiapp.enums.productService.PRODUCT_TITLE;
import com.kudiapp.kudiapp.enums.productService.ServiceProductStatus;
import com.kudiapp.kudiapp.enums.productService.UrgencyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProductResponse {

    private Long id;
    private Category category;
    private String categoryItems;
    private PRODUCT_TITLE productTitle;
    private String productDescription;
    private UrgencyType urgencyType;
    private ServiceProductStatus status;
    private Map<String, Object> metadata;
    private Boolean canBeActivated;
    private String activationBlockerMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
