package com.vednovak.manager.product.data.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

import static com.vednovak.manager.product.utils.ProductConstants.*;

@Data
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = IS_REQUIRED_MESSAGE)
    @Size(min = CODE_LENGTH, max = CODE_LENGTH, message = CODE_LENGTH_VALIDATION_MESSAGE)
    @Pattern(regexp = ALLOWED_TEXT_REGEX, message = ALLOWED_TEXT_REGEX_VALIDATION_MESSAGE)
    private String code;

    @NotBlank(message = IS_REQUIRED_MESSAGE)
    @Size(max = NAME_MAX_LENGTH, message = NAME_LENGTH_VALIDATION_MESSAGE)
    @Pattern(regexp = ALLOWED_TEXT_REGEX, message = ALLOWED_TEXT_REGEX_VALIDATION_MESSAGE)
    private String name;

    @NotNull(message = IS_REQUIRED_MESSAGE)
    @DecimalMin(value = PRICE_MINIMUM, message = PRICE_MINIMUM_VALIDATION_MESSAGE)
    @Digits(integer = PRICE_MAX_INTEGER_DIGITS, fraction = PRICE_MAX_FRACTION_DIGITS, message = PRICE_FORMAT_VALIDATION_MESSAGE)
    private BigDecimal priceEur;

    private boolean isAvailable; // Default value is false
}
