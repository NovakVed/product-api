package com.vednovak.manager.handlers.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import static com.vednovak.manager.product.utils.ProductConstants.ALLOWED_TEXT_REGEX_VALIDATION_MESSAGE;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorData(
        @Schema(description = "The error message description", example = ALLOWED_TEXT_REGEX_VALIDATION_MESSAGE) String error,
        @Schema(description = "In case of failed validation, name of the field that failed to get validated", example = "code") String field) {

    public static ErrorData withError(String error) {
        return new ErrorData(error, null);
    }

    public static ErrorData empty() {
        return new ErrorData(EMPTY, EMPTY);
    }
}