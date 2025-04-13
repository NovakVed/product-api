package com.vednovak.manager.handlers.data;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.vednovak.manager.product.utils.ProductConstants.ALLOWED_TEXT_REGEX_VALIDATION_MESSAGE;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public record ErrorData(
        @Schema(description = "The error message description", example = ALLOWED_TEXT_REGEX_VALIDATION_MESSAGE)
        String error,
        @Schema(description = "In case of failed validation, name of the field that failed to get validated",
                example = "code")
        String field
) {

    public ErrorData(final String error) {
        this(error, null);
    }

    public static ErrorData empty() {
        return new ErrorData(EMPTY, EMPTY);
    }
}