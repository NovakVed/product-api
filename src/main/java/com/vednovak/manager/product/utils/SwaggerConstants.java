package com.vednovak.manager.product.utils;

public final class SwaggerConstants {

    public static final String OK = "200";
    public static final String BAD_REQUEST = "400";
    public static final String NOT_FOUND = "404";
    public static final String INTERNAL_SERVER_ERROR = "500";
    public static final String GET = "GET";

    private SwaggerConstants() {
        throw new IllegalStateException("Utility class");
    }
}