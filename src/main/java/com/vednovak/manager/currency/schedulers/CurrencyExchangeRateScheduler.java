package com.vednovak.manager.currency.schedulers;

import com.vednovak.manager.currency.services.CurrencyExchangeRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class CurrencyExchangeRateScheduler {

    private final CurrencyExchangeRateService currencyExchangeRateService;
    private final Set<String> supportedCurrencies;

    public CurrencyExchangeRateScheduler(
            final CurrencyExchangeRateService currencyExchangeRateService,
            @Value("${supported.currencies}") final Set<String> supportedCurrencies) {
        this.currencyExchangeRateService = currencyExchangeRateService;
        this.supportedCurrencies = supportedCurrencies;
    }

    @Scheduled(cron = "${cron.update.supported.currencies.exchange.rate}")
    public void updateExchangeRates() {
        try {
            currencyExchangeRateService.refreshExchangeRates(supportedCurrencies);
        } catch (final Exception e) {
            log.error("Error during scheduled exchange rate update", e);
        }
    }
}
