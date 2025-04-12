package com.vednovak.manager.product.mappers;

import com.vednovak.manager.product.data.dtos.ProductRequest;
import com.vednovak.manager.product.data.dtos.ProductResponse;
import com.vednovak.manager.product.data.models.Product;

import java.math.BigDecimal;

public interface ProductMapper {
    Product mapToProduct(ProductRequest productRequest);
    ProductResponse mapToProductResponse(Product product, BigDecimal sellingRate);
}
