package com.vednovak.manager.currency.services;

import java.math.BigDecimal;

public interface CurrencyConversionService {

    BigDecimal convertPrice(BigDecimal basePrice, String currency);
}
