package com.clothes.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequest {
    @NotNull
    @JsonProperty("productId")
    private Long variantId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
