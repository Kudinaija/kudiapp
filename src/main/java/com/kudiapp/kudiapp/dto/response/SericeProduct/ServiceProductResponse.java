package com.kudiapp.kudiapp.dto.response.SericeProduct;

import com.kudiapp.kudiapp.enums.Category;
import com.kudiapp.kudiapp.enums.PRODUCT_TITLE;
import com.kudiapp.kudiapp.enums.UrgencyType;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ServiceProductResponse {

    private Long id;

    private Category category;

    private String categoryItems;

    private PRODUCT_TITLE productTitle;

    private String productDescription;

    private UrgencyType urgentTypes;

    private Map<String, Object> metadata;
}
