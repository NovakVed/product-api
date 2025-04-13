package com.vednovak.manager.currency.services;

import com.vednovak.manager.currency.exceptions.CurrencyExchangeRateException;

import java.math.BigDecimal;

public interface CurrencyConversionService {

    BigDecimal convertPrice(BigDecimal basePrice, String currency) throws CurrencyExchangeRateException;
}
