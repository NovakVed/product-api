package com.vednovak.manager.currency.schedulers;

import com.vednovak.manager.currency.services.CurrencyExchangeRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CurrencyExchangeRateScheduler {

    private final CurrencyExchangeRateService currencyExchangeRateService;

    public CurrencyExchangeRateScheduler(final CurrencyExchangeRateService currencyExchangeRateService) {
        this.currencyExchangeRateService = currencyExchangeRateService;
    }

    @Scheduled(cron = "${cron.update.supported.currencies.exchange.rate}")
    public void updateExchangeRates() {
        try {
            currencyExchangeRateService.updateExchangeRates();
        } catch (final Exception e) {
            log.error("Error during scheduled exchange rate update", e);
        }
    }
}
