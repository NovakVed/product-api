package com.vednovak.manager.product.services.impl;

import com.vednovak.manager.currency.services.CurrencyConversionService;
import com.vednovak.manager.message.services.MessageService;
import com.vednovak.manager.product.data.models.Product;
import com.vednovak.manager.product.data.dtos.ProductRequest;
import com.vednovak.manager.product.data.dtos.ProductResponse;
import com.vednovak.manager.product.exceptions.DuplicateProductCodeException;
import com.vednovak.manager.product.exceptions.ProductNotFoundByCodeException;
import com.vednovak.manager.product.mappers.ProductMapper;
import com.vednovak.manager.product.repositories.ProductRepository;
import com.vednovak.manager.product.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.vednovak.manager.product.utils.ProductConstants.ERROR_PRODUCT_NOT_FOUND;

// TODO: add final to everything where you can!
// TODO: apply best clean code practices
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
            final MessageService messageService
    ) {
        this.currencyConversionService = currencyConversionService;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.messageService = messageService;
    }

    @Override
    public ProductResponse createProduct(final ProductRequest request) {
        final String productCode = request.getCode();
        if (productCodeExists(productCode)) {
            log.warn("Product with code '{}' already exists", productCode);
            throw new DuplicateProductCodeException(messageService.getMessage(ERROR_PRODUCT_NOT_FOUND));
        }

        final Product createdProduct = saveProduct(request);
        return createProductResponseFromProduct(createdProduct);
    }

    private boolean productCodeExists(final String code) {
        return productRepository.existsByCode(code);
    }

    // TODO: add try & catch to handle .save(product)
    //  IllegalArgumentException + OptimisticLockingFailureException
    private Product saveProduct(final ProductRequest request) {
        final Product product = productMapper.mapToProduct(request);
        return productRepository.save(product);
    }

    @Override
    public ProductResponse getProductByProductCode(final String code) {
        final Product product = findProductByCodeOrElseThrow(code);
        return createProductResponseFromProduct(product);
    }

    private Product findProductByCodeOrElseThrow(final String code) {
        return productRepository
                .findByCode(code)
                .orElseThrow(() -> {
                    log.error("Product with code '{}' not found", code);
                    return new ProductNotFoundByCodeException(messageService.getMessage(ERROR_PRODUCT_NOT_FOUND));
                });
    }

    // TODO: maybe use productMapper.mapToProductRespone instead of this method? check to see what is more practical!
    private ProductResponse createProductResponseFromProduct(final Product product) {
        final BigDecimal basePrice = product.getPriceEur();
        final BigDecimal sellingRate = currencyConversionService.convertPrice(basePrice, "USD"); // TODO: add supported currencies here
        return productMapper.mapToProductResponse(product, sellingRate);
    }

    // TODO: what if list is empty? or if some err occurs?
    // I am ok with handling this scenarios on FE and that we return empty list, like this: []
    @Override
    public List<ProductResponse> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::createProductResponseFromProduct)
                .toList();
    }
}