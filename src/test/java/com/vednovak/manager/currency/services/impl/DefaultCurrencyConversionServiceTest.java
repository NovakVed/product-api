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
import java.util.Map;
import java.util.Set;

import static com.vednovak.manager.currency.utils.CurrencyConstants.ERROR_CURRENCY_NOT_SUPPORTED;
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
                Set.of(EUR_CURRENCY_CODE, USD_CURRENCY_CODE),
                currencyExchangeRateService,
                messageService);
    }

    @Test
    @DisplayName("When base price is null, then throw exception")
    void whenBasePriceIsNull_ThenThrowException() {
        assertThatThrownBy(() -> defaultCurrencyConversionService.convertPrice(null, USD_CURRENCY_CODE))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("basePrice must not be null");
    }

    @Test
    @DisplayName("When currency is blank, then throw exception")
    void whenCurrencyIsBlank_ThenThrowException() {
        assertThatThrownBy(() -> defaultCurrencyConversionService.convertPrice(BigDecimal.TEN, StringUtils.SPACE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("currency must not be blank");
    }

    @Test
    @DisplayName("When base price is negative, then throw exception")
    void whenBasePriceIsNegative_ThenThrowException() {
        when(messageService.getMessage(ERROR_INVALID_BASE_PRICE)).thenReturn("Invalid base price");

        final BigDecimal basePrice = BigDecimal.valueOf(-10);

        assertThatThrownBy(() -> defaultCurrencyConversionService.convertPrice(basePrice, USD_CURRENCY_CODE))
                .isInstanceOf(CurrencyExchangeRateException.class)
                .hasMessage("Invalid base price");

        verify(messageService, times(1)).getMessage(ERROR_INVALID_BASE_PRICE);
    }

    @Test
    @DisplayName("When currency is not supported, then throw exception")
    void whenCurrencyIsNotSupported_ThenThrowException() {
        when(messageService.getMessage(ERROR_CURRENCY_NOT_SUPPORTED, HRK_CURRENCY_CODE))
                .thenReturn("Currency not supported");

        assertThatThrownBy(() -> defaultCurrencyConversionService.convertPrice(BigDecimal.TEN, HRK_CURRENCY_CODE))
                .isInstanceOf(CurrencyExchangeRateException.class)
                .hasMessage("Currency not supported");

        verify(messageService, times(WANTED_NUMBER_OF_INVOCATIONS)).getMessage(ERROR_CURRENCY_NOT_SUPPORTED, HRK_CURRENCY_CODE);
    }

    @Test
    @DisplayName("When exchange rate is available, then return converted price")
    void whenExchangeRateIsAvailable_ThenReturnConvertedPrice() {
        when(currencyExchangeRateService.getExchangeRates()).thenReturn(Map.of(USD_CURRENCY_CODE, BigDecimal.valueOf(1.2)));

        final BigDecimal result = defaultCurrencyConversionService.convertPrice(BigDecimal.TEN, USD_CURRENCY_CODE);

        assertThat(result).isEqualTo(BigDecimal.valueOf(12.00).setScale(2, RoundingMode.HALF_UP));
        verify(currencyExchangeRateService, times(WANTED_NUMBER_OF_INVOCATIONS)).getExchangeRates();
    }

    @Test
    @DisplayName("When exchange rate is missing, then throw exception")
    void whenExchangeRateIsMissing_ThenThrowException() {
        when(currencyExchangeRateService.getExchangeRates()).thenReturn(Map.of());

        assertThatThrownBy(() -> defaultCurrencyConversionService.convertPrice(BigDecimal.TEN, USD_CURRENCY_CODE))
                .isInstanceOf(NullPointerException.class);

        verify(currencyExchangeRateService, times(1)).getExchangeRates();
    }
}
