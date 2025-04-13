package com.vednovak.manager.handlers;

import com.vednovak.manager.currency.exceptions.CurrencyExchangeRateException;
import com.vednovak.manager.currency.exceptions.EmptySupportedCurrenciesConfig;
import com.vednovak.manager.handlers.data.ErrorData;
import com.vednovak.manager.handlers.data.ErrorDataList;
import com.vednovak.manager.product.exceptions.DuplicateProductCodeException;
import com.vednovak.manager.product.exceptions.ProductNotFoundException;
import com.vednovak.manager.product.exceptions.ProductSaveException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        final List<ErrorData> errorList = constructErrorDataList(
                ex.getBindingResult().getFieldErrors().stream()
                        .map(error -> new ErrorData(
                                error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid content",
                                error.getField()))
                        .toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorDataList.forErrors(errorList));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDataList> handleConstraintViolationException(final ConstraintViolationException ex) {
        log.debug("Constraint violation occurred", ex);

        final List<ErrorData> errorList = constructErrorDataList(
                ex.getConstraintViolations().stream()
                        .map(violation -> new ErrorData(
                                violation.getMessage(),
                                violation.getPropertyPath().toString()))
                        .toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorDataList.forErrors(errorList));
    }

    private List<ErrorData> constructErrorDataList(final List<ErrorData> errorDataList) {
        return errorDataList.stream()
                .map(error -> isNull(error) ? ErrorData.empty() : error)
                .toList();
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorData> handleProductNotFoundByCodeException(final ProductNotFoundException ex) {
        log.error("Product not found. {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorData.withError(ex.getMessage()));
    }

    @ExceptionHandler(CurrencyExchangeRateException.class)
    public ResponseEntity<ErrorData> handleProductNotFoundByCodeException(final CurrencyExchangeRateException ex) {
        log.error("Error fetching exchange rates from {}", ex.getMessage(), ex);

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
        log.error("Failed to save product: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorData.withError(ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorData> handleUnexpectedExceptions(final RuntimeException ex) {
        log.error("Unexpected application error occurred", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorData.withError("An unexpected application error occurred, please try again later or contact user service"));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorData> handleNullPointerExceptions(final NullPointerException ex) {
        log.error("NullPointer exception occurred", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorData.withError(ex.getMessage()));
    }

    @ExceptionHandler(EmptySupportedCurrenciesConfig.class)
    public ResponseEntity<ErrorData> handleEmptySupportedCurrenciesConfigExceptions(final EmptySupportedCurrenciesConfig ex) {
        log.error("Error supported currencies list in application.properties are empty. " +
                "Please check your configuration: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorData.withError(ex.getMessage()));
    }
}