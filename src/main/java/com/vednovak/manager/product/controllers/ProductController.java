package com.vednovak.manager.product.controllers;

import com.vednovak.manager.product.models.ProductRequest;
import com.vednovak.manager.product.models.ProductResponse;
import com.vednovak.manager.product.services.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(ProductController.ENDPOINT)
@Tag(name = "Product API", description = "ADD SOME DESCRIPTION HERE") // TODO: add desc
public class ProductController {

    static final String ENDPOINT = "/v1/products";

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest) {
        ProductResponse createdProduct = productService.createProduct(productRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{code}")
                .buildAndExpand(createdProduct.code())
                .toUri();
        return ResponseEntity.created(location).body(createdProduct);
    }

    @GetMapping(value = "/{productCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    // TODO: add validation
    public ProductResponse getProduct(@PathVariable String productCode) {
        return productService.getProductByProductCode(productCode);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProductResponse> getProducts() {
        return productService.getProducts();
    }
}