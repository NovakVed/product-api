package com.vednovak.manager.currency.services;

import java.math.BigDecimal;

public interface CurrencyExchangeRateService {

    void updateExchangeRates();
    BigDecimal convertPriceForCurrency(BigDecimal basePrice, String currency);
}
