package com.vednovak.manager.product.data.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

import static com.vednovak.manager.product.utils.ProductConstants.*;

@Data
@AllArgsConstructor
@Schema(description = "Represents the request payload for creating or updating a product")
public class ProductRequest {

    @Schema(description = "Unique code of the product", example = "A000000001")
    @NotBlank(message = IS_REQUIRED_MESSAGE)
    @Size(min = CODE_LENGTH, max = CODE_LENGTH, message = CODE_LENGTH_VALIDATION_MESSAGE)
    @Pattern(regexp = ALLOWED_CODE_REGEX, message = ALLOWED_CODE_REGEX_VALIDATION_MESSAGE)
    private String code;

    @Schema(description = "Name of the product", example = "Apple iMug Pro")
    @NotBlank(message = IS_REQUIRED_MESSAGE)
    @Size(max = NAME_MAX_LENGTH, message = NAME_LENGTH_VALIDATION_MESSAGE)
    @Pattern(regexp = ALLOWED_TEXT_REGEX, message = ALLOWED_TEXT_REGEX_VALIDATION_MESSAGE)
    private String name;

    @Schema(description = "Price of the product in EUR", example = "29.99")
    @NotNull(message = IS_REQUIRED_MESSAGE)
    @DecimalMin(value = PRICE_MINIMUM, message = PRICE_MINIMUM_VALIDATION_MESSAGE)
    @Digits(integer = PRICE_MAX_INTEGER_DIGITS, fraction = PRICE_MAX_FRACTION_DIGITS, message = PRICE_FORMAT_VALIDATION_MESSAGE)
    private BigDecimal priceEur;

    @Schema(description = "Indicates whether the product is available", example = "true", defaultValue = "false")
    private boolean isAvailable;
}
