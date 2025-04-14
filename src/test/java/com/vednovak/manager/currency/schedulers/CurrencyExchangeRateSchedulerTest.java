package com.vednovak.manager.currency.schedulers;

import com.vednovak.manager.currency.CurrencyBaseTestUtils;
import com.vednovak.manager.currency.services.CurrencyExchangeRateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class CurrencyExchangeRateSchedulerTest extends CurrencyBaseTestUtils {

    private static final String RUNTIME_EXCEPTION_MESSAGE = "Test exception";

    @MockitoBean
    private CurrencyExchangeRateService currencyExchangeRateService;

    @Test
    @DisplayName("When updateExchangeRates is called, then CurrencyExchangeRateService updates exchange rates")
    void whenUpdateExchangeRatesCalled_ThenServiceUpdatesExchangeRates() {
        new CurrencyExchangeRateScheduler(currencyExchangeRateService).updateExchangeRates();

        verify(currencyExchangeRateService, times(WANTED_NUMBER_OF_INVOCATIONS)).updateExchangeRates();
    }

    @Test
    @DisplayName("When updateExchangeRates throws exception, then it is logged without propagation")
    void whenUpdateExchangeRatesThrowsException_ThenLogError() {
        doThrow(new RuntimeException(RUNTIME_EXCEPTION_MESSAGE)).when(currencyExchangeRateService).updateExchangeRates();

        new CurrencyExchangeRateScheduler(currencyExchangeRateService).updateExchangeRates();

        verify(currencyExchangeRateService, times(WANTED_NUMBER_OF_INVOCATIONS)).updateExchangeRates();
    }
}
