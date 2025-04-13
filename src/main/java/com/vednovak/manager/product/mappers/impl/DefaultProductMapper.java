package com.vednovak.manager.product.mappers.impl;

import com.vednovak.manager.product.data.dtos.ProductRequest;
import com.vednovak.manager.product.data.dtos.ProductResponse;
import com.vednovak.manager.product.data.models.Product;
import com.vednovak.manager.product.mappers.ProductMapper;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.vednovak.manager.product.utils.ProductConstants.NULL_PARAMETER_ERROR_MESSAGE_TEMPLATE;

@Component
public class DefaultProductMapper implements ProductMapper {

    @Override
    public Product mapToProduct(final ProductRequest productRequest) throws NullPointerException {
        Validate.notNull(productRequest, NULL_PARAMETER_ERROR_MESSAGE_TEMPLATE.formatted("ProductRequest"));

        return Product.builder()
                .code(productRequest.getCode())
                .name(productRequest.getName())
                .priceEur(productRequest.getPriceEur())
                .isAvailable(Boolean.TRUE.equals(productRequest.isAvailable()))
                .build();
    }

    @Override
    public ProductResponse mapToProductResponse(final Product product, final BigDecimal sellingRate) throws NullPointerException {
        Validate.notNull(product, NULL_PARAMETER_ERROR_MESSAGE_TEMPLATE.formatted("Product"));
        Validate.notNull(sellingRate, NULL_PARAMETER_ERROR_MESSAGE_TEMPLATE.formatted("sellingRate"));

        return new ProductResponse(
                product.getCode(),
                product.getName(),
                product.getPriceEur(),
                sellingRate,
                product.getIsAvailable()
        );
    }
}