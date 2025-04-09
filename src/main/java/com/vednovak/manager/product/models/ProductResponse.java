package com.vednovak.manager.product.models;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

// TODO: add schema for all!
public record ProductResponse(
        @Schema(
                description = "Product Code",
                type = "string"
        )
        String code,
        String name,
        BigDecimal priceEur,
        BigDecimal priceUsd,
        boolean isAvailable
) {
    public static ProductResponse fromProduct(Product product) {
        return new ProductResponse(
                product.getCode(),
                product.getName(),
                product.getPriceEur(),
                product.getPriceUsd(),
                product.getIsAvailable()
        );
    }
}
