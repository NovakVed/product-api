package com.vednovak.manager.currency.services;

import com.vednovak.manager.currency.data.dtos.CurrencyExchangeRateData;
import com.vednovak.manager.currency.exceptions.CurrencyExchangeRateException;
import com.vednovak.manager.currency.exceptions.EmptySupportedCurrenciesConfig;

import java.util.Set;

public interface ExchangeRateValidationService {

    void validateSupportedCurrenciesNotEmpty() throws EmptySupportedCurrenciesConfig;

    void validateFetchedExchangeRates(Set<CurrencyExchangeRateData> fetchedExchangeRates) throws CurrencyExchangeRateException;
}
