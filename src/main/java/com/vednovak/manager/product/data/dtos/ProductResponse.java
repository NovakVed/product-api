package com.vednovak.manager.product.data.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Represents the response containing product details")
public record ProductResponse(
        @Schema(description = "Unique code of the product", example = "A000000001")
        String code,

        @Schema(description = "Name of the product", example = "Apple iMug Pro")
        String name,

        @Schema(description = "Price of the product in EUR", example = "29.99")
        BigDecimal priceEur,

        @Schema(description = "Price of the product in USD", example = "34.99")
        BigDecimal priceUsd,

        @Schema(description = "Indicates whether the product is available", example = "true", defaultValue = "false")
        boolean isAvailable) {
}