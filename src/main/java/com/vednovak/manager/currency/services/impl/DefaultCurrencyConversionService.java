package com.vednovak.manager.currency.services.impl;

import com.vednovak.manager.currency.exceptions.CurrencyExchangeRateException;
import com.vednovak.manager.currency.services.CurrencyConversionService;
import com.vednovak.manager.currency.services.CurrencyExchangeRateService;
import com.vednovak.manager.message.services.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

import static com.vednovak.manager.currency.utils.CurrencyConstants.*;

@Slf4j
@Service
@PropertySource("classpath:currencies.properties")
public class DefaultCurrencyConversionService implements CurrencyConversionService {

    private static final int ROUNDING_SCALE = 2;

    private final Set<String> supportedCurrencies;
    private final CurrencyExchangeRateService currencyExchangeRateService;
    private final MessageService messageService;

    public DefaultCurrencyConversionService(
            @Value("${supported.currencies}") final Set<String> supportedCurrencies,
            CurrencyExchangeRateService currencyExchangeRateService,
            MessageService messageService) {
        this.supportedCurrencies = supportedCurrencies;
        this.currencyExchangeRateService = currencyExchangeRateService;
        this.messageService = messageService;
    }

    @Override
    public BigDecimal convertPrice(BigDecimal basePrice, String currency) {
        Validate.notNull(basePrice, NULL_PARAMETER_ERROR_MESSAGE_TEMPLATE.formatted("BigDecimal"));
        Validate.notBlank(currency, BLANK_PARAMETER_ERROR_MESSAGE_TEMPLATE.formatted("String"));

        validateBasePrice(basePrice);
        validateCurrencySupport(currency);

        BigDecimal exchangeRate = getExchangeRateForCurrency(currency);

        return calculateConvertedPrice(basePrice, exchangeRate);
    }

    private void validateBasePrice(final BigDecimal basePrice) {
        if (basePrice.compareTo(BigDecimal.ZERO) < 0) {
            log.error("Invalid base price: {}. It must be a non-negative value.", basePrice);
            throw new CurrencyExchangeRateException(messageService.getMessage(ERROR_INVALID_BASE_PRICE));
        }
    }

    private void validateCurrencySupport(final String currency) {
        if (!supportedCurrencies.contains(currency)) {
            log.error("Invalid currency: {}. It is not supported.", currency);
            throw new CurrencyExchangeRateException(messageService.getMessage(ERROR_CURRENCY_NOT_SUPPORTED, currency));
        }
    }

    private BigDecimal getExchangeRateForCurrency(final String currency) {
        return currencyExchangeRateService.getExchangeRates().get(currency);
    }

    private BigDecimal calculateConvertedPrice(final BigDecimal basePrice, final BigDecimal exchangeRate) {
        return basePrice.multiply(exchangeRate).setScale(ROUNDING_SCALE, RoundingMode.HALF_UP);
    }
}
