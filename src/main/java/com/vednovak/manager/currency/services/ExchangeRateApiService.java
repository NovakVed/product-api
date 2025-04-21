package com.vednovak.manager.currency.services;

import com.vednovak.manager.currency.data.dtos.CurrencyExchangeRateData;
import com.vednovak.manager.currency.exceptions.CurrencyExchangeRateException;

import java.util.Set;

public interface ExchangeRateApiService {

    Set<CurrencyExchangeRateData> fetchExchangeRates(Set<String> forCurrencies) throws CurrencyExchangeRateException;
}
