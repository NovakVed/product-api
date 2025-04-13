package com.vednovak.manager.product.services;


import com.vednovak.manager.product.data.dtos.ProductRequest;
import com.vednovak.manager.product.data.dtos.ProductResponse;
import com.vednovak.manager.product.exceptions.DuplicateProductCodeException;
import com.vednovak.manager.product.exceptions.ProductNotFoundException;
import com.vednovak.manager.product.exceptions.ProductSaveException;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productRequest) throws ProductSaveException, DuplicateProductCodeException, NullPointerException;

    ProductResponse findProductByCode(String code) throws ProductNotFoundException, NullPointerException;

    List<ProductResponse> getProducts() throws NullPointerException;
}
