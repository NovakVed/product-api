package com.vednovak.manager.product.data.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

// TODO: double check all validations
@Data
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "")
    @Size(min = 10, max = 10, message = "")
    private String code;

    @NotBlank(message = "")
    private String name;

    @NotBlank(message = "")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 38, fraction = 2, message = "Price format is invalid")
    private BigDecimal priceEur;

    // TODO: add that default value is false if not provided, if you even need that?
    // TODO: add some validation?
    private boolean isAvailable;
}
