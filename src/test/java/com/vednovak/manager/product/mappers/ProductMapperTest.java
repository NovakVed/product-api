package com.vednovak.manager.product.mappers;

import com.vednovak.manager.product.ProductBaseTestUtils;
import com.vednovak.manager.product.data.dtos.ProductRequest;
import com.vednovak.manager.product.data.dtos.ProductResponse;
import com.vednovak.manager.product.data.models.Product;
import com.vednovak.manager.product.mappers.impl.DefaultProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ProductMapperTest extends ProductBaseTestUtils {

    private final DefaultProductMapper productMapper = new DefaultProductMapper();

    @Test
    @DisplayName("Given valid ProductRequest, when mapping to Product, then return valid Product")
    void givenValidProductRequest_WhenMappingToProduct_ThenReturnValidProduct() {
        final ProductRequest productRequest = createTestProductRequest();
        final Product product = productMapper.mapToProduct(productRequest);

        assertThat(product).isNotNull();
        assertThat(product.getCode()).isEqualTo(productRequest.getCode());
        assertThat(product.getName()).isEqualTo(productRequest.getName());
        assertThat(product.getPriceEur()).isEqualTo(productRequest.getPriceEur());
        assertThat(product.getIsAvailable()).isTrue();
    }

    @Test
    @DisplayName("Given valid Product and sellingRate, when mapping to ProductResponse, then return valid ProductResponse")
    void givenValidProductAndSellingRate_WhenMappingToProductResponse_ThenReturnValidProductResponse() {
        final Product product = createTestProduct();
        final BigDecimal sellingRate = BigDecimal.valueOf(999.99);

        final ProductResponse productResponse = productMapper.mapToProductResponse(product, sellingRate);

        assertThat(productResponse).isNotNull();
        assertThat(productResponse.code()).isEqualTo(product.getCode());
        assertThat(productResponse.name()).isEqualTo(product.getName());
        assertThat(productResponse.priceEur()).isEqualTo(product.getPriceEur());
        assertThat(productResponse.priceUsd()).isEqualTo(sellingRate);
        assertThat(productResponse.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("Given null ProductRequest, when mapping to Product, then throw IllegalArgumentException")
    void givenNullProductRequest_WhenMappingToProduct_ThenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> productMapper.mapToProduct(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("ProductRequest must not be null");
    }

    @Test
    @DisplayName("Given null Product, when mapping to ProductResponse, then throw IllegalArgumentException")
    void givenNullProduct_WhenMappingToProductResponse_ThenThrowIllegalArgumentException() {
        final BigDecimal sellingRate = BigDecimal.valueOf(1.1);

        assertThatThrownBy(() -> productMapper.mapToProductResponse(null, sellingRate))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Product must not be null");
    }

    @Test
    @DisplayName("Given null sellingRate, when mapping to ProductResponse, then throw IllegalArgumentException")
    void givenNullSellingRate_WhenMappingToProductResponse_ThenThrowIllegalArgumentException() {
        final Product product = createTestProduct();

        assertThatThrownBy(() -> productMapper.mapToProductResponse(product, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("sellingRate must not be null");
    }
}
