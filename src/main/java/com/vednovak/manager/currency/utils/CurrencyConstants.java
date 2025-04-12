package com.vednovak.manager.currency.utils;

public class CurrencyConstants {

    public static final String ERROR_BLANK_CURRENCY = "error.blank.currency";
    public static final String ERROR_INVALID_BASE_PRICE = "error.invalid.base.price";
    public static final String ERROR_CURRENCY_NOT_SUPPORTED = "error.currency.not.supported";
    public static final String ERROR_FAILED_EXCHANGE_RATE_DATA = "error.failed.exchange.rate.data";
    public static final String ERROR_UNEXPECTED_EXCHANGE_RATE_UPDATE = "error.unexpected.exchange.rate.update";

    public static final String QUERY_STRING_QUESTION_MARK = "?";
    public static final String QUERY_STRING_SEPARATOR = "&";

    public static final String RADIX_CHARACTER_SEARCH = ",";
    public static final String RADIX_CHARACTER_REPLACEMENT = ".";

    public static final String JSON_PROPERTY_CURRENCY = "valuta";
    public static final String JSON_PROPERTY_SELLING_RATE = "prodajni_tecaj";

    private CurrencyConstants() {
        throw new IllegalStateException("Utility class");
    }
}
