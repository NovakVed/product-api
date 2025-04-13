package com.vednovak.manager.product;

import com.vednovak.manager.product.data.dtos.ProductRequest;
import com.vednovak.manager.product.data.dtos.ProductResponse;
import com.vednovak.manager.product.data.models.Product;

import java.math.BigDecimal;

public abstract class ProductBaseTestUtils {

    protected static final String VALID_PRODUCT_ONE_CODE = "A000000001";
    protected static final String VALID_PRODUCT_ONE_NAME = "Apple iPhone 14 Pro";
    protected static final String VALID_PRODUCT_TWO_CODE = "S000000001";
    protected static final String VALID_PRODUCT_TWO_NAME = "Samsung Galaxy S22";

    protected ProductResponse createTestProductResponseAvailable() {
        return new ProductResponse(
                VALID_PRODUCT_ONE_CODE,
                VALID_PRODUCT_ONE_NAME,
                BigDecimal.valueOf(29.99),
                BigDecimal.valueOf(34.99),
                true);
    }

    protected ProductResponse createTestProductResponseUnavailable() {
        return new ProductResponse(
                VALID_PRODUCT_TWO_CODE,
                VALID_PRODUCT_TWO_NAME,
                BigDecimal.valueOf(19.99),
                BigDecimal.valueOf(24.99),
                false);
    }

    protected ProductRequest createTestProductRequest() {
        return new ProductRequest(
                VALID_PRODUCT_ONE_CODE,
                VALID_PRODUCT_ONE_NAME,
                BigDecimal.valueOf(29.99),
                true);
    }

    protected Product createTestProduct() {
        return Product.builder()
                .code(VALID_PRODUCT_ONE_CODE)
                .name(VALID_PRODUCT_ONE_NAME)
                .priceEur(BigDecimal.valueOf(999.99))
                .isAvailable(true)
                .build();
    }

    protected String createProductJson(
            String code,
            String name,
            BigDecimal priceEur,
            BigDecimal priceUsd) {
        return String.format("""
                {
                    "code": "%s",
                    "name": "%s",
                    "priceEur": %s,
                    "priceUsd": %s,
                    "isAvailable": %s
                }
                """, code, name, priceEur, priceUsd, true);
    }
}
