package com.clothes.backend.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String categoryName;
    private String brandName;
    private String imageUrl;
    private boolean isFeatured;
    private BigDecimal discountPrice;
    private List<VariantResponse> variants;
    private int totalStock;
    private Double averageRating;
    private int reviewCount;
}
