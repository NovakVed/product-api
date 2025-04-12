package com.vednovak.manager.product.controllers;

import com.vednovak.manager.product.data.dtos.ProductRequest;
import com.vednovak.manager.product.data.dtos.ProductResponse;
import com.vednovak.manager.product.services.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

// TODO: add swagger doc!
@RestController
@RequestMapping(ProductController.ENDPOINT)
@Tag(name = "Product API", description = "ADD SOME DESCRIPTION HERE") // TODO: add desc
public class ProductController {

    static final String ENDPOINT = "/v1/products";

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // TODO: return 201 Created instead!
    // TODO: return correct http status codes for each request!
    // TODO: add auth for CRUD operations on product
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody final ProductRequest productRequest) {
        final ProductResponse createdProduct = productService.createProduct(productRequest);
        // TODO: check why do you need location?
        final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{code}")
                .buildAndExpand(createdProduct.code())
                .toUri();

        return ResponseEntity.created(location).body(createdProduct);
    }

    @GetMapping(value = "/{productCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    // TODO: add validation
    public ProductResponse getProduct(@PathVariable final String productCode) {
        return productService.getProductByProductCode(productCode);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProductResponse> getProducts() {
        return productService.getProducts();
    }

    // TODO: add put and delete!

    // TODO: maybe add getAvailableProducts?
}