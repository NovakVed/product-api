package com.vednovak.manager.currency.services.impl;

import com.vednovak.manager.currency.data.dtos.CurrencyExchangeRateData;
import com.vednovak.manager.currency.exceptions.CurrencyExchangeRateException;
import com.vednovak.manager.currency.exceptions.EmptySupportedCurrenciesConfig;
import com.vednovak.manager.currency.services.CurrencyExchangeRateService;
import com.vednovak.manager.currency.services.ExchangeRateApiService;
import com.vednovak.manager.currency.services.ExchangeRateValidationService;
import com.vednovak.manager.message.services.MessageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.vednovak.manager.currency.utils.CurrencyConstants.*;

@Slf4j
@Service
@PropertySource("classpath:currencies.properties")
public class DefaultCurrencyExchangeRateService implements CurrencyExchangeRateService {

    private final Map<String, BigDecimal> exchangeRatesCache;
    private final ExchangeRateApiService exchangeRateApiService;
    private final ExchangeRateValidationService exchangeRateValidationService;
    private final MessageService messageService;
    private final Set<String> supportedCurrencies;

    public DefaultCurrencyExchangeRateService(
            final ExchangeRateApiService exchangeRateApiService,
            final ExchangeRateValidationService exchangeRateValidationService,
            final MessageService messageService,
            @Value("${supported.currencies}") final Set<String> supportedCurrencies) {
        this.exchangeRateApiService = exchangeRateApiService;
        this.exchangeRateValidationService = exchangeRateValidationService;
        this.messageService = messageService;
        this.supportedCurrencies = supportedCurrencies;
        this.exchangeRatesCache = new ConcurrentHashMap<>(supportedCurrencies.size());
    }

    @PostConstruct
    public void initializeExchangeRates() {
        exchangeRateValidationService.validateSupportedCurrenciesNotEmpty();
        refreshExchangeRates(supportedCurrencies);
    }

    @Override
    public void refreshExchangeRates(final Set<String> forCurrencies)
            throws CurrencyExchangeRateException, EmptySupportedCurrenciesConfig {
        exchangeRateValidationService.validateIfCurrenciesAreSupported(forCurrencies);
        fetchAndUpdateExchangeRates(forCurrencies);
    }

    private void fetchAndUpdateExchangeRates(final Set<String> forCurrencies) {
        try {
            final Set<CurrencyExchangeRateData> fetchedExchangeRates = exchangeRateApiService
                    .fetchExchangeRates(forCurrencies);
            exchangeRateValidationService.validateFetchedExchangeRates(fetchedExchangeRates);
            fetchedExchangeRates.forEach(this::cacheExchangeRate);
            removeUnsupportedCurrenciesFromCache();
        } catch (final Exception ex) {
            log.error("Unexpected error during exchange rate update", ex);
            throw new CurrencyExchangeRateException(messageService.getMessage(ERROR_UNEXPECTED_EXCHANGE_RATE_UPDATE));
        }
    }

    private void cacheExchangeRate(final CurrencyExchangeRateData exchangeRate)
            throws UnsupportedOperationException, ClassCastException, NullPointerException, IllegalArgumentException {
        final String currency = exchangeRate.getCurrency();
        final BigDecimal sellingRate = parseSellingRate(exchangeRate.getSellingRate());
        exchangeRatesCache.put(currency, sellingRate);
    }

    private BigDecimal parseSellingRate(final String currencySellingRate) throws NumberFormatException {
        final String normalizedRate = StringUtils.replace(currencySellingRate, RADIX_CHARACTER_SEARCH,
                RADIX_CHARACTER_REPLACEMENT);
        return new BigDecimal(normalizedRate);
    }

    private void removeUnsupportedCurrenciesFromCache() throws UnsupportedOperationException, ClassCastException {
        exchangeRatesCache.keySet().removeIf(currency -> {
            if (!supportedCurrencies.contains(currency)) {
                log.info("Removing unsupported currency '{}' from cache.", currency);
                return true;
            }
            return false;
        });
    }

    @Override
    public BigDecimal findOrFetchExchangeRate(final String fromCurrency) throws CurrencyExchangeRateException {
        exchangeRateValidationService.validateCurrencyIsSupport(fromCurrency);
        try {
            refetchAndRepopulateCacheIfMissing(fromCurrency);
            return exchangeRatesCache.get(fromCurrency);
        } catch (final NullPointerException ex) {
            throw new CurrencyExchangeRateException(
                    messageService.getMessage(ERROR_EXCHANGE_RAGE_NOT_FOUND_IN_CACHE, fromCurrency));
        }
    }

    private void refetchAndRepopulateCacheIfMissing(final String currency) throws CurrencyExchangeRateException {
        if (!exchangeRatesCache.containsKey(currency)) {
            log.info("Exchange rate for '{}' not found in cache. Fetching from API and repopulating cache...", currency);
            refreshExchangeRates(Set.of(currency));
        }
    }
}
