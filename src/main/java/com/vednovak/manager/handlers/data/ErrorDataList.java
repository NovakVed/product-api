package com.vednovak.manager.handlers.data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Represents the response containing list of error messages")
public record ErrorDataList(@Schema(description = "List of errors") List<ErrorData> errors) {

    public static ErrorDataList forErrors(List<ErrorData> errors) {
        return new ErrorDataList(errors);
    }
}