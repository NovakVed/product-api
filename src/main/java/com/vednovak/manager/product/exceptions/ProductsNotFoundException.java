package com.vednovak.manager.product.exceptions;

// TODO - dovrsi
public class ProductsNotFoundException extends RuntimeException {

    public ProductsNotFoundException(String message) {
        super(message);
    }
}
