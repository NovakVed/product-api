package com.vednovak.manager.currency.services.impl;

import com.vednovak.manager.currency.exceptions.CurrencyExchangeRateException;
import com.vednovak.manager.currency.services.CurrencyConversionService;
import com.vednovak.manager.currency.services.CurrencyExchangeRateService;
import com.vednovak.manager.message.services.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.vednovak.manager.currency.utils.CurrencyConstants.*;

@Slf4j
@Service
@PropertySource("classpath:currencies.properties")
public class DefaultCurrencyConversionService implements CurrencyConversionService {

    private static final int ROUNDING_SCALE = 2;

    private final CurrencyExchangeRateService currencyExchangeRateService;
    private final MessageService messageService;

    public DefaultCurrencyConversionService(
            CurrencyExchangeRateService currencyExchangeRateService,
            MessageService messageService) {
        this.currencyExchangeRateService = currencyExchangeRateService;
        this.messageService = messageService;
    }

    @Override
    public BigDecimal convertToCurrency(final BigDecimal basePrice, final String currency) throws CurrencyExchangeRateException {
        validateInputs(basePrice, currency);

        final BigDecimal exchangeRate = currencyExchangeRateService.findOrFetchExchangeRate(currency);
        return calculateConvertedPrice(basePrice, exchangeRate);
    }

    private void validateInputs(final BigDecimal basePrice, final String currency)
            throws CurrencyExchangeRateException {
        Validate.notNull(basePrice, NULL_PARAMETER_ERROR_MESSAGE_TEMPLATE.formatted("basePrice"));
        Validate.notBlank(currency, BLANK_PARAMETER_ERROR_MESSAGE_TEMPLATE.formatted("currency"));

        validatePrice(basePrice);
    }

    private void validatePrice(final BigDecimal basePrice) throws CurrencyExchangeRateException {
        if (basePrice.compareTo(BigDecimal.ZERO) < 0) {
            log.error("Invalid base price: {}. It must be a non-negative value.", basePrice);
            throw new CurrencyExchangeRateException(messageService.getMessage(ERROR_INVALID_BASE_PRICE));
        }
    }

    private BigDecimal calculateConvertedPrice(final BigDecimal basePrice, final BigDecimal exchangeRate) {
        return basePrice.multiply(exchangeRate).setScale(ROUNDING_SCALE, RoundingMode.HALF_UP);
    }
}
