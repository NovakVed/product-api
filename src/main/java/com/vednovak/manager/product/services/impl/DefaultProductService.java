package com.vednovak.manager.product.services.impl;

import com.vednovak.manager.product.exceptions.DuplicateProductCodeException;
import com.vednovak.manager.product.exceptions.ProductNotFoundByCodeException;
import com.vednovak.manager.product.models.Product;
import com.vednovak.manager.product.models.ProductRequest;
import com.vednovak.manager.product.models.ProductResponse;
import com.vednovak.manager.product.repositories.ProductRepository;
import com.vednovak.manager.product.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

// TODO: fix all!
// TODO: add final to everything where you can!
@Slf4j
@Service
public class DefaultProductService implements ProductService {

    private final ProductRepository productRepository;

    public DefaultProductService(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // TODO: use StringUtils.join instead of "+code+"
    @Override
    public ProductResponse createProduct(ProductRequest request) {
        String productCode = request.getCode();
        if (doesProductCodeExist(productCode)) {
            log.warn("Product with code '{}' already exists", productCode);
            throw new DuplicateProductCodeException("Product with code '" + productCode + "' already exists");
        }

        Product createdProduct = saveProduct(request);
        return createProductResponseFromProduct(createdProduct);
    }

    private boolean doesProductCodeExist(String code) {
        return productRepository.existsByCode(code);
    }

    // TODO: add try & catch to handle .save(product)
    //  IllegalArgumentException + OptimisticLockingFailureException
    private Product saveProduct(ProductRequest request) {
        final Product product = mapProductRequestToProduct(request);
        return productRepository.save(product);
    }

    private Product mapProductRequestToProduct(ProductRequest request) {
        return Product.builder()
                .code(request.getCode())
                .name(request.getName())
                .priceEur(request.getPriceEur())
                .isAvailable(Boolean.TRUE.equals(request.isAvailable()))
                .build();
    }

    @Override
    public ProductResponse getProductByProductCode(String code) {
        Product product = getProductByCodeOrElseThrow(code);
        return createProductResponseFromProduct(product);
    }

    private Product getProductByCodeOrElseThrow(String code) {
        return productRepository
                .findByCode(code)
                .orElseThrow(() -> {
                    log.error("Product with code '{}' not found", code);
                    return new ProductNotFoundByCodeException("Product with code '" + code + "' not found");
                });
    }

    // TODO: usd?
    private ProductResponse createProductResponseFromProduct(Product product) {
        return new ProductResponse(product.getCode(),
                product.getName(),
                product.getPriceEur(),
                product.getPriceUsd(),
                product.getIsAvailable());
    }

    // TODO: what if list is empty? or if some err occurs?
    // I am ok with handling this scenarion on FE and that we return empty list, like this: []
    @Override
    public List<ProductResponse> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::fromProduct)
                .toList();
    }
}