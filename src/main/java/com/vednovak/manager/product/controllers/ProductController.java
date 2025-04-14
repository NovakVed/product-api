package com.vednovak.manager.product.controllers;

import com.vednovak.manager.handlers.data.ErrorData;
import com.vednovak.manager.handlers.data.ErrorDataList;
import com.vednovak.manager.product.data.dtos.ProductRequest;
import com.vednovak.manager.product.data.dtos.ProductResponse;
import com.vednovak.manager.product.exceptions.DuplicateProductCodeException;
import com.vednovak.manager.product.exceptions.ProductNotFoundException;
import com.vednovak.manager.product.exceptions.ProductSaveException;
import com.vednovak.manager.product.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import static com.vednovak.manager.product.utils.SwaggerConstants.*;

// TODO: add ci.yaml for git (CI pipeline)
// TODO: return correct http status codes for each request!
@RestController
@RequestMapping(ProductController.ENDPOINT)
@Validated
@Tag(name = "Products", description = "the Product API for managing products, including creation, retrieval, and listing")
public class ProductController {

    static final String ENDPOINT = "/v1/products";

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // TODO: potential security improvement add auth for CRUD operations on product
    @Operation(summary = "Create a new product", description = "Creates a new product and returns the created product details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = CREATED, description = "Product created successfully"),
            @ApiResponse(
                    responseCode = BAD_REQUEST,
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorDataList.class)
                    )),
            @ApiResponse(
                    responseCode = INTERNAL_SERVER_ERROR,
                    description = "Issue occurred when trying to create new product",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorData.class)
                    ))
    })
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid final ProductRequest productRequest)
            throws ProductSaveException, DuplicateProductCodeException, NullPointerException {
        final ProductResponse createdProduct = productService.createProduct(productRequest);

        final URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{code}")
                .buildAndExpand(createdProduct.code())
                .toUri();

        return ResponseEntity.created(location).body(createdProduct);
    }

    @Operation(summary = "Get product by code", description = "Retrieves a product by its unique code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK, description = "Product retrieved successfully"),
            @ApiResponse(
                    responseCode = BAD_REQUEST,
                    description = "Invalid product code",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorData.class)
                    )
            ),
            @ApiResponse(responseCode = NOT_FOUND,
                    description = "Product not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorData.class)
                    )
            ),
            @ApiResponse(
                    description = "Issue occurred when trying to fetch a product with provided code",
                    responseCode = INTERNAL_SERVER_ERROR,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorData.class)
                    )
            )
    })
    @GetMapping(value = "/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductResponse getProduct(
            @Parameter(
                    description = "Unique code of the product",
                    required = true,
                    example = "A000000001"
            )
            @PathVariable
            @Valid
            @NotBlank(message = IS_REQUIRED_MESSAGE)
            @Size(min = CODE_LENGTH, max = CODE_LENGTH, message = CODE_LENGTH_VALIDATION_MESSAGE)
            @Pattern(regexp = ALLOWED_CODE_REGEX, message = ALLOWED_CODE_REGEX_VALIDATION_MESSAGE) final String code)
            throws ProductNotFoundException, NullPointerException {
        return productService.findProductByCode(code);
    }

    @Operation(summary = "Get all products", description = "Retrieves a list of all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = OK, description = "Products retrieved successfully"),
            @ApiResponse(
                    responseCode = INTERNAL_SERVER_ERROR,
                    description = "Issue occurred when trying to fetch all products",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorData.class)
                    )
            )
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProductResponse> getProducts() throws NullPointerException {
        return productService.getProducts();
    }
}