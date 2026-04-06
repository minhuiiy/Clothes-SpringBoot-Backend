package com.clothes.backend.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class VariantRequest {
    private String size;
    private String color;
    private BigDecimal additionalPrice;
    private String sku;
    private Integer stock;
    private Integer lowStockThreshold;
}
