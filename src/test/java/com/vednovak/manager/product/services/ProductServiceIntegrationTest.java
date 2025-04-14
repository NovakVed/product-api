package com.vednovak.manager.product.services;

import com.vednovak.manager.product.ProductBaseTestUtils;
import com.vednovak.manager.product.data.dtos.ProductRequest;
import com.vednovak.manager.product.data.dtos.ProductResponse;
import com.vednovak.manager.product.exceptions.DuplicateProductCodeException;
import com.vednovak.manager.product.exceptions.ProductNotFoundException;
import com.vednovak.manager.product.exceptions.ProductSaveException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductServiceIntegrationTest extends ProductBaseTestUtils {

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("Given valid ProductRequest, when creating product, then return valid ProductResponse")
    void givenValidProductRequest_WhenCreatingProduct_ThenReturnValidProductResponse()
            throws ProductSaveException, DuplicateProductCodeException {
        final ProductRequest productRequest = createTestProductRequest();

        final ProductResponse productResponse = productService.createProduct(productRequest);

        assertThat(productResponse).isNotNull();
        assertThat(productResponse.code()).isEqualTo(productRequest.getCode());
        assertThat(productResponse.name()).isEqualTo(productRequest.getName());
        assertThat(productResponse.priceEur()).isEqualTo(productRequest.getPriceEur());
        assertThat(productResponse.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("Given duplicate ProductRequest, when creating product, then throw DuplicateProductCodeException")
    @Sql("/data-test.sql")
    void givenDuplicateProductRequest_WhenCreatingProduct_ThenThrowDuplicateProductCodeException()
            throws DuplicateProductCodeException {
        final ProductRequest productRequest = createTestProductRequest();


        productService.createProduct(productRequest);

        assertThatThrownBy(() -> productService.createProduct(productRequest))
                .isInstanceOf(DuplicateProductCodeException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Given null ProductRequest, when creating product, then throw NullPointerException")
    void givenNullProductRequest_WhenCreatingProduct_ThenThrowNullPointerException() {
        assertThatThrownBy(() -> productService.createProduct(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null");
    }

    @Test
    @DisplayName("Given existing product code, when finding product, then return valid ProductResponse")
    @Sql("/data-test.sql")
    void givenExistingProductCode_WhenFindingProduct_ThenReturnValidProductResponse() {
        final ProductRequest productRequest = createTestProductRequest();
        productService.createProduct(productRequest);

        final ProductResponse productResponse = productService.findProductByCode(productRequest.getCode());

        assertThat(productResponse).isNotNull();
        assertThat(productResponse.code()).isEqualTo(productRequest.getCode());
        assertThat(productResponse.name()).isEqualTo(productRequest.getName());
    }

    @Test
    @DisplayName("Given non-existing product code, when finding product, then throw ProductNotFoundException")
    void givenNonExistingProductCode_WhenFindingProduct_ThenThrowProductNotFoundException() {
        final String nonExistingCode = "NONEXISTENT";

        assertThatThrownBy(() -> productService.findProductByCode(nonExistingCode))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Given null product code, when finding product, then throw NullPointerException")
    void givenNullProductCode_WhenFindingProduct_ThenThrowNullPointerException() {
        assertThatThrownBy(() -> productService.findProductByCode(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be blank");
    }

    @Test
    @DisplayName("When getting all products, then return list of ProductResponses")
    @Sql("/data-test.sql")
    void whenGettingAllProducts_ThenReturnListOfProductResponses() {
        final List<ProductResponse> products = productService.getProducts();

        assertThat(products).isNotNull().hasSizeGreaterThan(1);
    }

    @Test
    @DisplayName("When no products exist, then getProducts returns empty list")
    @Sql(statements = "DELETE FROM products")
    void whenNoProductsExist_ThenGetProductsReturnsEmptyList() {
        final List<ProductResponse> products = productService.getProducts();

        assertThat(products).isNotNull().isEmpty();
    }
}
