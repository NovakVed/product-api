package com.vednovak.manager.product.controllers;

import com.vednovak.manager.product.data.dtos.ProductRequest;
import com.vednovak.manager.product.data.dtos.ProductResponse;
import com.vednovak.manager.product.services.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.vednovak.manager.product.utils.ProductConstants.*;

// TODO: add ci.yaml for git (CI pipeline)
// TODO: add swagger doc!
// TODO: return correct http status codes for each request!
@RestController
@RequestMapping(ProductController.ENDPOINT)
@Validated
@Tag(name = "Product API", description = "ADD SOME DESCRIPTION HERE") // TODO: add desc
public class ProductController {

    static final String ENDPOINT = "/v1/products";

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // TODO: add auth for CRUD operations on product
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid final ProductRequest productRequest) {
        final ProductResponse createdProduct = productService.createProduct(productRequest);
        // TODO: check why do you need location?
        final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{code}")
                .buildAndExpand(createdProduct.code())
                .toUri();

        return ResponseEntity.created(location).body(createdProduct);
    }

    @GetMapping(value = "/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductResponse getProduct(
            @PathVariable
            @Valid
            @NotBlank(message = IS_REQUIRED_MESSAGE)
            @Size(min = 10, max = 10, message = "must be exactly 10 characters")
            @Pattern(regexp = ALLOWED_TEXT_REGEX, message = ALLOWED_TEXT_REGEX_VALIDATION_MESSAGE) final String code) {
        return productService.findProductByCode(code);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProductResponse> getProducts() {
        return productService.getProducts();
    }

    // TODO: add put and delete!

    // TODO: maybe add getAvailableProducts?
}