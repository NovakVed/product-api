package com.vednovak.manager.currency.data.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import static com.vednovak.manager.currency.utils.CurrencyConstants.JSON_PROPERTY_CURRENCY;
import static com.vednovak.manager.currency.utils.CurrencyConstants.JSON_PROPERTY_SELLING_RATE;

@Data
public class CurrencyExchangeRateData {

    @JsonProperty(JSON_PROPERTY_CURRENCY)
    private String currency;

    @JsonProperty(JSON_PROPERTY_SELLING_RATE)
    private String sellingRate;
}