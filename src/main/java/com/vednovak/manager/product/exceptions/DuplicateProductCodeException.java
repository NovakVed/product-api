package com.vednovak.manager.product.exceptions;

public class DuplicateProductCodeException extends RuntimeException {

  public DuplicateProductCodeException(String message) {
    super(message);
  }
}
