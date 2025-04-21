package com.vednovak.manager.currency.services.impl;

import com.vednovak.manager.currency.CurrencyBaseTestUtils;
import com.vednovak.manager.currency.exceptions.CurrencyExchangeRateException;
import com.vednovak.manager.currency.services.CurrencyExchangeRateService;
import com.vednovak.manager.message.services.MessageService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.vednovak.manager.currency.utils.CurrencyConstants.ERROR_INVALID_BASE_PRICE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultCurrencyConversionServiceTest extends CurrencyBaseTestUtils {

    @Mock
    private CurrencyExchangeRateService currencyExchangeRateService;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private DefaultCurrencyConversionService defaultCurrencyConversionService;

    @BeforeEach
    void setUp() {
        defaultCurrencyConversionService = new DefaultCurrencyConversionService(
                currencyExchangeRateService,
                messageService);
    }

    @Test
    @DisplayName("When base price is null, then throw exception")
    void whenBasePriceIsNull_ThenThrowException() {
        assertThatThrownBy(() -> defaultCurrencyConversionService.convertToCurrency(null, USD_CURRENCY_CODE))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("basePrice must not be null");
    }

    @Test
    @DisplayName("When currency is blank, then throw exception")
    void whenCurrencyIsBlank_ThenThrowException() {
        assertThatThrownBy(() -> defaultCurrencyConversionService.convertToCurrency(BigDecimal.TEN, StringUtils.SPACE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("currency must not be blank");
    }

    @Test
    @DisplayName("When base price is negative, then throw exception")
    void whenBasePriceIsNegative_ThenThrowException() {
        when(messageService.getMessage(ERROR_INVALID_BASE_PRICE)).thenReturn("Invalid base price");

        final BigDecimal basePrice = BigDecimal.valueOf(-10);

        assertThatThrownBy(() -> defaultCurrencyConversionService.convertToCurrency(basePrice, USD_CURRENCY_CODE))
                .isInstanceOf(CurrencyExchangeRateException.class)
                .hasMessage("Invalid base price");

        verify(messageService, times(SINGLE_INVOCATION)).getMessage(ERROR_INVALID_BASE_PRICE);
    }

    @Test
    @DisplayName("When currency is not supported, then throw exception")
    void whenCurrencyIsNotSupported_ThenThrowException() {
        when(currencyExchangeRateService.findOrFetchExchangeRate(HRK_CURRENCY_CODE))
                .thenThrow(CurrencyExchangeRateException.class);

        assertThatThrownBy(() -> defaultCurrencyConversionService.convertToCurrency(BigDecimal.TEN, HRK_CURRENCY_CODE))
                .isInstanceOf(CurrencyExchangeRateException.class);
    }

    @Test
    @DisplayName("When exchange rate is available, then return converted price")
    void whenExchangeRateIsAvailable_ThenReturnConvertedPrice() {
        when(currencyExchangeRateService.findOrFetchExchangeRate(USD_CURRENCY_CODE))
                .thenReturn(BigDecimal.valueOf(1.2));

        final BigDecimal result = defaultCurrencyConversionService.convertToCurrency(BigDecimal.TEN, USD_CURRENCY_CODE);

        assertThat(result).isEqualTo(BigDecimal.valueOf(12.00).setScale(2, RoundingMode.HALF_UP));
        verify(currencyExchangeRateService, times(SINGLE_INVOCATION)).findOrFetchExchangeRate(USD_CURRENCY_CODE);
    }
}
