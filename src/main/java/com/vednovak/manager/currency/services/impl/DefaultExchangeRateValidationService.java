package com.vednovak.manager.currency.services.impl;

import com.vednovak.manager.currency.data.dtos.CurrencyExchangeRateData;
import com.vednovak.manager.currency.exceptions.CurrencyExchangeRateException;
import com.vednovak.manager.currency.services.ExchangeRateValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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
        if (fetchedExchangeRates == null || fetchedExchangeRates.isEmpty()) {
            throw new CurrencyExchangeRateException("Invalid fetched exchange rates or supported currencies.");
        }

        if (fetchedExchangeRates.size() != supportedCurrencies.size()) {
            logMissingCurrencies(fetchedExchangeRates);
        }
    }

    private void logMissingCurrencies(Set<CurrencyExchangeRateData> fetchedExchangeRates) {
        Set<String> fetchedCurrencies = fetchedExchangeRates.stream()
                .map(CurrencyExchangeRateData::getCurrency)
                .collect(Collectors.toSet());

        Set<String> missingCurrencies = supportedCurrencies.stream()
                .filter(currency -> !fetchedCurrencies.contains(currency))
                .collect(Collectors.toSet());

        if (!missingCurrencies.isEmpty()) {
            log.warn("The following supported currencies are missing from the fetched exchange rates: {}",
                    missingCurrencies);
        }
    }
}