package com.vednovak.manager.product.mappers.impl;

import com.vednovak.manager.product.data.dtos.ProductRequest;
import com.vednovak.manager.product.data.dtos.ProductResponse;
import com.vednovak.manager.product.data.models.Product;
import com.vednovak.manager.product.mappers.ProductMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DefaultProductMapper implements ProductMapper {

    @Override
    public Product mapToProduct(ProductRequest request) {
        return Product.builder()
                .code(request.getCode())
                .name(request.getName())
                .priceEur(request.getPriceEur())
                .isAvailable(Boolean.TRUE.equals(request.isAvailable()))
                .build();
    }

    @Override
    public ProductResponse mapToProductResponse(Pair<Product, BigDecimal> productAndSellingRate) {
        final Product product = productAndSellingRate.getLeft();
        final BigDecimal sellingRate = productAndSellingRate.getRight();

        return new ProductResponse(
                product.getCode(),
                product.getName(),
                product.getPriceEur(),
                sellingRate,
                product.getIsAvailable()
        );
    }
}
