package com.kudiapp.kudiapp.dto.request;

import com.kudiapp.kudiapp.enums.Category;
import com.kudiapp.kudiapp.enums.PRODUCT_TITLE;
import com.kudiapp.kudiapp.enums.UrgencyType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ServiceProductRequest {

    @NotNull
    private Category category;

    private String categoryItems;

    @NotNull
    private PRODUCT_TITLE productTitle;

    private String productDescription;

    private UrgencyType urgentTypes;

    private Map<String, Object> metadata;
}
