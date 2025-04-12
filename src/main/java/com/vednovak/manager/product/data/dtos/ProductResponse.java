package com.vednovak.manager.product.data.dtos;

import java.math.BigDecimal;

public record ProductResponse(
        // TODO: add @Schema s
        String code,
        String name,
        BigDecimal priceEur,
        BigDecimal priceUsd, // change to Map<String, BigDecimal> supportedPrices
        boolean isAvailable
) {
}