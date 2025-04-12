package com.vednovak.manager.currency.services.impl;

import com.vednovak.manager.currency.data.dtos.CurrencyExchangeRateData;
import com.vednovak.manager.currency.exceptions.CurrencyExchangeRateException;
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

    private final Map<String, BigDecimal> exchangeRates;
    private final ExchangeRateApiService exchangeRateApiService;
    private final ExchangeRateValidationService validationService;
    private final MessageService messageService;
    private final Set<String> supportedCurrencies;

    public DefaultCurrencyExchangeRateService(
            final ExchangeRateApiService exchangeRateApiService,
            final ExchangeRateValidationService validationService,
            final MessageService messageService,
            @Value("${supported.currencies}") final Set<String> supportedCurrencies) {
        this.exchangeRateApiService = exchangeRateApiService;
        this.validationService = validationService;
        this.messageService = messageService;
        this.supportedCurrencies = supportedCurrencies;
        this.exchangeRates = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void initializeExchangeRates() {
        updateExchangeRates();
    }

    @Override
    public void updateExchangeRates() {
        try {
            final Set<CurrencyExchangeRateData> fetchedExchangeRates = exchangeRateApiService.fetchExchangeRates();
            validationService.validateFetchedExchangeRates(fetchedExchangeRates);
            removeNoLongerSupportedExchangeRates();
            fetchedExchangeRates.forEach(this::saveExchangeRate);
        } catch (final Exception ex) {
            log.error("Unexpected error during exchange rate update", ex);
            throw new CurrencyExchangeRateException(messageService.getMessage(ERROR_UNEXPECTED_EXCHANGE_RATE_UPDATE));
        }
    }

    private void removeNoLongerSupportedExchangeRates() {
        exchangeRates.keySet().removeIf(currency -> !supportedCurrencies.contains(currency));
    }

    private void saveExchangeRate(final CurrencyExchangeRateData exchangeRate) {
        final String currency = exchangeRate.getCurrency();
        final BigDecimal sellingRate = parseSellingRate(exchangeRate.getSellingRate());
        exchangeRates.put(currency, sellingRate);
    }

    private BigDecimal parseSellingRate(final String currencySellingRate) {
        final String normalizedRate = StringUtils.replace(currencySellingRate, RADIX_CHARACTER_SEARCH, RADIX_CHARACTER_REPLACEMENT);
        return new BigDecimal(normalizedRate);
    }

    @Override
    public Map<String, BigDecimal> getExchangeRates() {
        return exchangeRates;
    }
}
