package com.vednovak.manager.product.repositories;

import com.vednovak.manager.product.data.models.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductRepositoryIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Given product exists by code, when checking existence, then return true")
    @Sql("/data-test.sql")
    void givenProductExistsByCodeWhenCheckingExistenceThenReturnTrue() {
        String existingCode = "TEST000009";

        boolean exists = productRepository.existsByCode(existingCode);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Given product does not exist by code, when checking existence, then return false")
    void givenProductDoesNotExistByCodeWhenCheckingExistenceThenReturnFalse() {
        String nonExistingCode = "NONEXISTENT";

        boolean exists = productRepository.existsByCode(nonExistingCode);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Given product exists by code, when finding product, then return product")
    void givenProductExistsByCodeWhenFindingProductThenReturnProduct() {
        String existingCode = "TEST000010";

        Optional<Product> product = productRepository.findByCode(existingCode);

        assertThat(product).isPresent();
        assertThat(product.get().getCode()).isEqualTo(existingCode);
    }

    @Test
    @DisplayName("Given product does not exist by code, when finding product, then return empty")
    void givenProductDoesNotExistByCodeWhenFindingProductThenReturnEmpty() {
        String nonExistingCode = "NONEXISTENT";

        Optional<Product> product = productRepository.findByCode(nonExistingCode);

        assertThat(product).isNotPresent();
    }
}
