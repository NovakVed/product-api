package com.vednovak.manager.currency.services;

import com.vednovak.manager.currency.data.dtos.CurrencyExchangeRateData;

import java.util.Set;

public interface ExchangeRateValidationService {

    void validateFetchedExchangeRates(Set<CurrencyExchangeRateData> fetchedExchangeRates);
}
