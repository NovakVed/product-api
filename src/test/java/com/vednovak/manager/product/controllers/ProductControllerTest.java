package com.vednovak.manager.product.controllers;

import com.vednovak.manager.product.ProductBaseTestUtils;
import com.vednovak.manager.product.data.dtos.ProductRequest;
import com.vednovak.manager.product.data.dtos.ProductResponse;
import com.vednovak.manager.product.exceptions.ProductNotFoundException;
import com.vednovak.manager.product.services.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest extends ProductBaseTestUtils {

    private static final String INVALID_CODE = "INVALID_CODE";
    private static final String NON_EXISTENT_CODE = "1111111111";
    private static final String SHORT_CODE = "123456789";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private String getProductEndpointWithPath(String code) {
        return ProductController.ENDPOINT + "/" + code;
    }

    @Test
    @DisplayName("Given valid product code, when retrieving product, then return product details")
    void givenValidCode_WhenGetProduct_ThenReturnProductDetails() throws Exception {
        final ProductResponse productResponse = createTestProductResponseAvailable();

        when(productService.findProductByCode(VALID_PRODUCT_ONE_CODE))
                .thenReturn(createTestProductResponseAvailable());

        mockMvc.perform(get(getProductEndpointWithPath(VALID_PRODUCT_ONE_CODE)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(productResponse.code()))
                .andExpect(jsonPath("$.name").value(productResponse.name()))
                .andExpect(jsonPath("$.priceEur").value(productResponse.priceEur()))
                .andExpect(jsonPath("$.priceUsd").value(productResponse.priceUsd()))
                .andExpect(jsonPath("$.isAvailable").value(productResponse.isAvailable()));
    }

    @Test
    @DisplayName("Given invalid product code format, when retrieving product, then return 400 Bad Request")
    void givenInvalidCodeFormat_WhenGetProduct_ThenReturnBadRequest() throws Exception {
        when(productService.findProductByCode(INVALID_CODE))
                .thenThrow(new IllegalArgumentException("must contain only alphanumeric characters"));

        mockMvc.perform(get(getProductEndpointWithPath(INVALID_CODE)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("Given non-existent product code, when retrieving product, then return 404 Not Found")
    void givenNonExistentCode_WhenGetProduct_ThenReturnNotFound() throws Exception {
        final String productNotFoundMessage = "Product not found for code: " + NON_EXISTENT_CODE;

        when(productService.findProductByCode(NON_EXISTENT_CODE))
                .thenThrow(new ProductNotFoundException(productNotFoundMessage));

        mockMvc.perform(get(getProductEndpointWithPath(NON_EXISTENT_CODE)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").value(productNotFoundMessage));
    }

    @Test
    @DisplayName("Given valid product code, when unexpected exception occurs, then return 500 Internal Server Error")
    void givenValidCode_WhenUnexpectedException_ThenReturnInternalServerError() throws Exception {
        when(productService.findProductByCode(VALID_PRODUCT_ONE_CODE)).thenThrow(RuntimeException.class);

        mockMvc.perform(get(getProductEndpointWithPath(VALID_PRODUCT_ONE_CODE)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Given blank product code, when retrieving product, then return 400 Bad Request")
    void givenBlankCode_WhenGetProduct_ThenReturnBadRequest() throws Exception {
        mockMvc.perform(get(getProductEndpointWithPath(StringUtils.SPACE)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("Given invalid product code length, when retrieving product, then return 400 Bad Request")
    void givenInvalidCodeLengthWhenGetProductThenReturnBadRequest() throws Exception {
        final String errorMessage = "must be exactly 10 characters";

        mockMvc.perform(get(getProductEndpointWithPath(SHORT_CODE)).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors[0].error").value(errorMessage));
    }

    @Test
    @DisplayName("Given products exist, when retrieving all products, then return list of products")
    void givenProductsExist_WhenGetProducts_ThenReturnProductList() throws Exception {
        ProductResponse product1 = createTestProductResponseAvailable();
        ProductResponse product2 = createTestProductResponseUnavailable();

        when(productService.getProducts()).thenReturn(List.of(product1, product2));

        mockMvc.perform(get(ProductController.ENDPOINT).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].code").value(product1.code()))
                .andExpect(jsonPath("$[0].name").value(product1.name()))
                .andExpect(jsonPath("$[0].priceEur").value(product1.priceEur()))
                .andExpect(jsonPath("$[0].priceUsd").value(product1.priceUsd()))
                .andExpect(jsonPath("$[0].isAvailable").value(product1.isAvailable()))
                .andExpect(jsonPath("$[1].code").value(product2.code()))
                .andExpect(jsonPath("$[1].name").value(product2.name()))
                .andExpect(jsonPath("$[1].priceEur").value(product2.priceEur()))
                .andExpect(jsonPath("$[1].priceUsd").value(product2.priceUsd()))
                .andExpect(jsonPath("$[1].isAvailable").value(product2.isAvailable()));
    }

    @Test
    @DisplayName("Given unexpected exception, when retrieving all products, then return 500 Internal Server Error")
    void givenUnexpectedException_WhenGetProducts_ThenReturnInternalServerError() throws Exception {
        when(productService.getProducts()).thenThrow(RuntimeException.class);

        mockMvc.perform(get(ProductController.ENDPOINT).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Given valid product data, when creating product, then return created product details")
    void givenValidProductData_WhenCreateProduct_ThenReturnCreatedProductDetails() throws Exception {
        ProductResponse createdProduct = createTestProductResponseAvailable();
        ProductRequest productRequest = createTestProductRequest();

        when(productService.createProduct(productRequest)).thenReturn(createdProduct);

        final String productRequestContent = createProductJson(
                VALID_PRODUCT_ONE_CODE,
                VALID_PRODUCT_ONE_NAME,
                BigDecimal.valueOf(29.99),
                BigDecimal.valueOf(34.99));

        mockMvc.perform(post(ProductController.ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestContent)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(createdProduct.code()))
                .andExpect(jsonPath("$.name").value(createdProduct.name()))
                .andExpect(jsonPath("$.priceEur").value(createdProduct.priceEur()))
                .andExpect(jsonPath("$.priceUsd").value(createdProduct.priceUsd()))
                .andExpect(jsonPath("$.isAvailable").value(createdProduct.isAvailable()));
    }

    @Test
    @DisplayName("Given invalid product data, when creating product, then return 400 Bad Request")
    void givenInvalidProductData_WhenCreateProduct_ThenReturnBadRequest() throws Exception {
        final String productRequest = createProductJson(
                StringUtils.EMPTY,
                StringUtils.EMPTY,
                BigDecimal.valueOf(-10),
                BigDecimal.valueOf(-15));

        mockMvc.perform(post(ProductController.ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("Given unexpected exception, when creating product, then return 500 Internal Server Error")
    void givenUnexpectedException_WhenCreateProduct_ThenReturnInternalServerError() throws Exception {
        ProductRequest productRequest = createTestProductRequest();

        when(productService.createProduct(productRequest)).thenThrow(RuntimeException.class);

        String productRequestContent = createProductJson(
                VALID_PRODUCT_ONE_CODE,
                VALID_PRODUCT_ONE_NAME,
                BigDecimal.valueOf(29.99),
                BigDecimal.valueOf(34.99));

        mockMvc.perform(post(ProductController.ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestContent)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").exists());
    }
}