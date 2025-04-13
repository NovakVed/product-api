package com.vednovak.manager.currency.exceptions;

public class EmptySupportedCurrenciesConfig extends RuntimeException {

    public EmptySupportedCurrenciesConfig(String message) {
        super(message);
    }
}
