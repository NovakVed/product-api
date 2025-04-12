package com.vednovak.manager.currency.services;

import java.math.BigDecimal;
import java.util.Map;

public interface CurrencyExchangeRateService {

    void updateExchangeRates();

    Map<String, BigDecimal> getExchangeRates();
}
