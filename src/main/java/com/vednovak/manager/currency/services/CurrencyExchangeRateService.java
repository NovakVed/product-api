package com.vednovak.manager.currency.services;

import com.vednovak.manager.currency.exceptions.CurrencyExchangeRateException;
import com.vednovak.manager.currency.exceptions.EmptySupportedCurrenciesConfig;

import java.math.BigDecimal;
import java.util.Map;

public interface CurrencyExchangeRateService {

    void updateExchangeRates() throws CurrencyExchangeRateException, EmptySupportedCurrenciesConfig;

    Map<String, BigDecimal> getExchangeRates();
}
