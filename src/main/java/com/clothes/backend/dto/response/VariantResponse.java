package com.clothes.backend.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class VariantResponse {
    private Long id;
    private String size;
    private String color;
    private BigDecimal additionalPrice;
    private String sku;
    private Integer stock;
}
