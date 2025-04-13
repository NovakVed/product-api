package com.vednovak.manager.handlers;

import com.vednovak.manager.currency.exceptions.CurrencyExchangeRateException;
import com.vednovak.manager.handlers.data.ErrorData;
import com.vednovak.manager.handlers.data.ErrorDataList;
import com.vednovak.manager.product.exceptions.DuplicateProductCodeException;
import com.vednovak.manager.product.exceptions.ProductNotFoundException;
import com.vednovak.manager.product.exceptions.ProductSaveException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDataList> handleValidationException(final MethodArgumentNotValidException ex) {
        log.debug("Data validation failed on request", ex);

        final List<ErrorData> errorList = constructFieldErrorDataList(ex.getBindingResult().getFieldErrors());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorDataList.forErrors(errorList));
    }

    private List<ErrorData> constructFieldErrorDataList(final List<FieldError> fieldErrors) {
        return fieldErrors.stream().map(error -> {
            if (isNull(error)) {
                return ErrorData.empty();
            }
            final String errorMessage = error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid content";
            return new ErrorData(errorMessage, error.getField());
        }).toList();
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorData> handleProductNotFoundByCodeException(final ProductNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorData.withError(ex.getMessage()));
    }

    @ExceptionHandler(CurrencyExchangeRateException.class)
    public ResponseEntity<ErrorData> handleProductNotFoundByCodeException(final CurrencyExchangeRateException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorData.withError(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateProductCodeException.class)
    public ResponseEntity<ErrorData> handleDuplicateProductException(final DuplicateProductCodeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorData.withError(ex.getMessage()));
    }

    @ExceptionHandler(ProductSaveException.class)
    public ResponseEntity<ErrorData> handleProductSaveException(final ProductSaveException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorData.withError(ex.getMessage()));
    }
}