package com.vednovak.manager.currency.services.impl;

import com.vednovak.manager.currency.exceptions.CurrencyExchangeRateException;
import com.vednovak.manager.currency.services.CurrencyConversionService;
import com.vednovak.manager.currency.services.CurrencyExchangeRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

@Slf4j
@Service
@PropertySource("classpath:currencies.properties")
public class DefaultCurrencyConversionService implements CurrencyConversionService {

    private final Set<String> supportedCurrencies;
    private final CurrencyExchangeRateService currencyExchangeRateService;

    public DefaultCurrencyConversionService(
            @Value("${supported.currencies}") final Set<String> supportedCurrencies,
            CurrencyExchangeRateService currencyExchangeRateService
    ) {
        this.supportedCurrencies = supportedCurrencies;
        this.currencyExchangeRateService = currencyExchangeRateService;
    }

    @Override
    public BigDecimal convertPrice(BigDecimal basePrice, String currency) {
        validateBasePrice(basePrice);
        validateCurrencySupport(currency);

        BigDecimal exchangeRate = getExchangeRateForCurrency(currency);

        return calculateConvertedPrice(basePrice, exchangeRate);
    }

    private void validateBasePrice(final BigDecimal basePrice) {
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new CurrencyExchangeRateException("Invalid base price.");
        }
    }

    private void validateCurrencySupport(final String currency) {
        if (!supportedCurrencies.contains(currency)) {
            throw new CurrencyExchangeRateException("Currency not supported.");
        }
    }

    private BigDecimal getExchangeRateForCurrency(final String currency) {
        return currencyExchangeRateService.getExchangeRates().getOrDefault(currency, BigDecimal.ZERO);
    }

    private BigDecimal calculateConvertedPrice(final BigDecimal basePrice, final BigDecimal exchangeRate) {
        return basePrice.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
    }
}
