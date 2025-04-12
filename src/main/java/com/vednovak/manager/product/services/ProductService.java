package com.vednovak.manager.product.services;


import com.vednovak.manager.product.data.dtos.ProductRequest;
import com.vednovak.manager.product.data.dtos.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest productRequest);
    ProductResponse getProductByProductCode(String code);
    List<ProductResponse> getProducts();
}
