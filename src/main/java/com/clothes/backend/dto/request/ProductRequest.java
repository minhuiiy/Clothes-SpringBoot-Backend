package com.clothes.backend.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private Long brandId;
    private String imageUrl;
    private boolean isFeatured;
    private Integer stock;
    private String color;
    private List<VariantRequest> variants;
}
