package com.vednovak.manager.product.utils;

public class ProductConstants {

    public static final String IS_REQUIRED_MESSAGE = "is required.";
    public static final String CODE_LENGTH_VALIDATION_MESSAGE = "must be exactly 10 characters.";
    public static final String NAME_LENGTH_VALIDATION_MESSAGE = "must not exceed 255 characters.";
    public static final String PRICE_MINIMUM_VALIDATION_MESSAGE = "must be greater than or equal to 0.";
    public static final String PRICE_FORMAT_VALIDATION_MESSAGE = "must have up to 38 digits and 2 decimal places.";
    public static final String ALLOWED_TEXT_REGEX = "^[a-zA-Z0-9\\s-]+$";
    public static final String ALLOWED_TEXT_REGEX_VALIDATION_MESSAGE = "must contain only alphanumeric and allowed characters";

    public static final int CODE_LENGTH = 10;
    public static final int NAME_MAX_LENGTH = 255;
    public static final String PRICE_MINIMUM = "0.00";
    public static final int PRICE_MAX_INTEGER_DIGITS = 38;
    public static final int PRICE_MAX_FRACTION_DIGITS = 2;

    public static final String ERROR_PRODUCT_NOT_FOUND  = "error.product.not.found";
    public static final String ERROR_PRODUCT_ALREADY_EXISTS  = "error.product.already.exists";
    public static final String ERROR_PRODUCT_SAVE  = "error.product.save";

    public static final String NULL_PARAMETER_ERROR_MESSAGE_TEMPLATE = "%s must not be null";

    private ProductConstants() {
        throw new IllegalStateException("Utility class");
    }
}
