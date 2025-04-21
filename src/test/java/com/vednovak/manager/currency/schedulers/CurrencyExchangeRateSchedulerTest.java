package com.vednovak.manager.currency.schedulers;

import com.vednovak.manager.currency.CurrencyBaseTestUtils;
import com.vednovak.manager.currency.services.CurrencyExchangeRateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyExchangeRateSchedulerTest extends CurrencyBaseTestUtils {

    private static final String RUNTIME_EXCEPTION_MESSAGE = "Test exception";

    @Mock
    private CurrencyExchangeRateService currencyExchangeRateService;

    @InjectMocks
    private CurrencyExchangeRateScheduler currencyExchangeRateScheduler;

    @Test
    @DisplayName("When updateExchangeRates is called, then CurrencyExchangeRateService updates exchange rates")
    void whenUpdateExchangeRatesCalled_ThenServiceUpdatesExchangeRates() {
        Set<String> supportedCurrencies = Set.of(USD_CURRENCY_CODE, EUR_CURRENCY_CODE);
        currencyExchangeRateScheduler = new CurrencyExchangeRateScheduler(currencyExchangeRateService, supportedCurrencies);

        currencyExchangeRateScheduler.updateExchangeRates();

        verify(currencyExchangeRateService, times(1)).refreshExchangeRates(supportedCurrencies);
    }

    @Test
    @DisplayName("When updateExchangeRates throws exception, then it is logged without propagation")
    void whenUpdateExchangeRatesThrowsException_ThenLogError() {
        Set<String> supportedCurrencies = Set.of(USD_CURRENCY_CODE, EUR_CURRENCY_CODE);
        currencyExchangeRateScheduler = new CurrencyExchangeRateScheduler(currencyExchangeRateService, supportedCurrencies);

        doThrow(new RuntimeException(RUNTIME_EXCEPTION_MESSAGE))
                .when(currencyExchangeRateService)
                .refreshExchangeRates(supportedCurrencies);

        currencyExchangeRateScheduler.updateExchangeRates();

        verify(currencyExchangeRateService, times(1)).refreshExchangeRates(supportedCurrencies);
    }
}
