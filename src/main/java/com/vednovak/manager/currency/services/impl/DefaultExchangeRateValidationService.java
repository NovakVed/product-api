package com.vednovak.manager.currency.services.impl;

import com.vednovak.manager.currency.data.dtos.CurrencyExchangeRateData;
import com.vednovak.manager.currency.exceptions.CurrencyExchangeRateException;
import com.vednovak.manager.currency.services.ExchangeRateValidationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@PropertySource("classpath:currencies.properties")
public class DefaultExchangeRateValidationService implements ExchangeRateValidationService {

    private final Set<String> supportedCurrencies;

    public DefaultExchangeRateValidationService(
            @Value("${supported.currencies}") final Set<String> supportedCurrencies) {
        this.supportedCurrencies = supportedCurrencies;
    }

    @Override
    public void validateFetchedExchangeRates(Set<CurrencyExchangeRateData> fetchedExchangeRates) {
        if (fetchedExchangeRates == null
                || fetchedExchangeRates.isEmpty()
                || fetchedExchangeRates.size() != supportedCurrencies.size()
                || !areAllSupportedCurrenciesPresent(fetchedExchangeRates, supportedCurrencies)) {
            throw new CurrencyExchangeRateException("Invalid fetched exchange rates or supported currencies.");
        }
    }

    private boolean areAllSupportedCurrenciesPresent(
            Set<CurrencyExchangeRateData> fetchedExchangeRates,
            Set<String> supportedCurrencies) {
        return fetchedExchangeRates.stream()
                .map(CurrencyExchangeRateData::getCurrency)
                .collect(Collectors.toSet())
                .containsAll(supportedCurrencies);
    }
}