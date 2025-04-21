package com.vednovak.manager.currency.services;

import com.vednovak.manager.currency.exceptions.CurrencyExchangeRateException;
import com.vednovak.manager.currency.exceptions.EmptySupportedCurrenciesConfig;

import java.math.BigDecimal;
import java.util.Set;

public interface CurrencyExchangeRateService {

    void refreshExchangeRates(final Set<String> forCurrencies) throws CurrencyExchangeRateException, EmptySupportedCurrenciesConfig;

    BigDecimal findOrFetchExchangeRate(String fromCurrency) throws CurrencyExchangeRateException;
}
