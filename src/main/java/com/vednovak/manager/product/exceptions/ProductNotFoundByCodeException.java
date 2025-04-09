package com.vednovak.manager.product.exceptions;

public class ProductNotFoundByCodeException extends RuntimeException {

  public ProductNotFoundByCodeException(String message) {
    super(message);
  }
}
