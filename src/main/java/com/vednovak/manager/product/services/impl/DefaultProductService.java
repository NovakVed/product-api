package com.vednovak.manager.product.services.impl;

import com.vednovak.manager.currency.services.CurrencyConversionService;
import com.vednovak.manager.message.services.MessageService;
import com.vednovak.manager.product.data.dtos.ProductRequest;
import com.vednovak.manager.product.data.dtos.ProductResponse;
import com.vednovak.manager.product.data.models.Product;
import com.vednovak.manager.product.exceptions.DuplicateProductCodeException;
import com.vednovak.manager.product.exceptions.ProductNotFoundException;
import com.vednovak.manager.product.exceptions.ProductSaveException;
import com.vednovak.manager.product.mappers.ProductMapper;
import com.vednovak.manager.product.repositories.ProductRepository;
import com.vednovak.manager.product.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.vednovak.manager.product.utils.ProductConstants.*;

@Slf4j
@Service
public class DefaultProductService implements ProductService {

    private final CurrencyConversionService currencyConversionService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final MessageService messageService;

    public DefaultProductService(
            final CurrencyConversionService currencyConversionService,
            final ProductRepository productRepository,
            final ProductMapper productMapper,
            final MessageService messageService) {
        this.currencyConversionService = currencyConversionService;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.messageService = messageService;
    }

    @Override
    public ProductResponse createProduct(final ProductRequest productRequest) {
        validateProductCodeUniqueness(productRequest.getCode());
        final Product createdProduct = saveProduct(productRequest);
        return mapToProductResponse(createdProduct);
    }

    private void validateProductCodeUniqueness(final String productCode) {
        if (productRepository.existsByCode(productCode)) {
            log.warn("Product with code '{}' already exists", productCode);
            throw new DuplicateProductCodeException(messageService.getMessage(ERROR_PRODUCT_ALREADY_EXISTS));
        }
    }

    private Product saveProduct(final ProductRequest productRequest) {
        try {
            final Product product = productMapper.mapToProduct(productRequest);
            return productRepository.save(product);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            log.error("Failed to save product: {}", e.getMessage(), e);
            throw new ProductSaveException(messageService.getMessage(ERROR_PRODUCT_SAVE));
        }
    }

    @Override
    public ProductResponse findProductByCode(final String productCode) {
        final Product product = findProductByCodeOrElseThrow(productCode);
        return mapToProductResponse(product);
    }

    private Product findProductByCodeOrElseThrow(final String productCode) {
        return productRepository.findByCode(productCode)
                .orElseThrow(() -> {
                    log.error("Product with code '{}' not found", productCode);
                    return new ProductNotFoundException(messageService.getMessage(ERROR_PRODUCT_NOT_FOUND));
                });
    }

    private ProductResponse mapToProductResponse(final Product product) {
        final BigDecimal basePrice = product.getPriceEur();
        final BigDecimal convertedPrice = currencyConversionService.convertPrice(basePrice, "USD"); // TODO: add supported currencies here
        return productMapper.mapToProductResponse(product, convertedPrice);
    }

    @Override
    public List<ProductResponse> getProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToProductResponse)
                .toList();
    }
}