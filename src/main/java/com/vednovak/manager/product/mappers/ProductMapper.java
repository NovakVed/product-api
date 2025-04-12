package com.vednovak.manager.product.mappers;

import com.vednovak.manager.product.data.dtos.ProductRequest;
import com.vednovak.manager.product.data.dtos.ProductResponse;
import com.vednovak.manager.product.data.models.Product;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;

public interface ProductMapper {
    Product mapToProduct(ProductRequest productRequest);
    ProductResponse mapToProductResponse(Pair<Product, BigDecimal> productAndSellingRate);
}
