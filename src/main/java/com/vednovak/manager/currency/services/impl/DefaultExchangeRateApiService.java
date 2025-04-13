package com.vednovak.manager.currency.services.impl;

import com.vednovak.manager.currency.data.dtos.CurrencyExchangeRateData;
import com.vednovak.manager.currency.exceptions.CurrencyExchangeRateException;
import com.vednovak.manager.currency.services.ExchangeRateApiService;
import com.vednovak.manager.message.services.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.vednovak.manager.currency.utils.CurrencyConstants.*;

@Slf4j
@Service
@PropertySource("classpath:currencies.properties")
public class DefaultExchangeRateApiService implements ExchangeRateApiService {

    private final String dateFormatForExchangeRateUrl;
    private final String exchangeRateBaseApiUrl;
    private final String queryCurrencyForExchangeRateUrl;
    private final String queryDateForExchangeRateUrl;
    private final Set<String> supportedCurrencies;
    private final MessageService messageService;
    private final RestTemplate restTemplate;

    public DefaultExchangeRateApiService(
            @Value("${exchange.rate.base.api.url}") final String exchangeRateBaseApiUrl,
            @Value("${exchange.rate.currency.query.api.url}") final String queryCurrencyForExchangeRateUrl,
            @Value("${exchange.rate.date.query.api.url}") final String queryDateForExchangeRateUrl,
            @Value("${exchange.rate.api.url.date.format}") final String dateFormatForExchangeRateUrl,
            @Value("${supported.currencies}") final Set<String> supportedCurrencies,
            final MessageService messageService,
            final RestTemplate restTemplate) {
        this.dateFormatForExchangeRateUrl = dateFormatForExchangeRateUrl;
        this.exchangeRateBaseApiUrl = exchangeRateBaseApiUrl;
        this.queryCurrencyForExchangeRateUrl = queryCurrencyForExchangeRateUrl;
        this.queryDateForExchangeRateUrl = queryDateForExchangeRateUrl;
        this.supportedCurrencies = supportedCurrencies;
        this.messageService = messageService;
        this.restTemplate = restTemplate;
    }

    @Override
    public Set<CurrencyExchangeRateData> fetchExchangeRates() throws CurrencyExchangeRateException {
        final String url = buildApiUrl();
        return fetchExchangeRatesFromApi(url).orElseThrow(() ->
             new CurrencyExchangeRateException(messageService.getMessage(ERROR_FAILED_EXCHANGE_RATE_DATA, url))
        );
    }

    private String buildApiUrl() {
        return StringUtils.join(
                exchangeRateBaseApiUrl, QUERY_STRING_QUESTION_MARK,
                buildDateQuery(), QUERY_STRING_SEPARATOR,
                buildCurrencyQuery());
    }

    private String buildDateQuery() {
        final String currentDate = getCurrentFormattedDate();
        return StringUtils.join(queryDateForExchangeRateUrl, currentDate);
    }

    private String getCurrentFormattedDate() {
        try {
            return LocalDate.now().format(DateTimeFormatter.ofPattern(dateFormatForExchangeRateUrl));
        } catch (final DateTimeException | IllegalArgumentException ex) {
            throw new CurrencyExchangeRateException(messageService.getMessage(ERROR_FAILED_DATE_TIME_FORMAT));
        }
    }

    private String buildCurrencyQuery() {
        return supportedCurrencies.stream()
                .map(currency -> StringUtils.join(queryCurrencyForExchangeRateUrl, currency))
                .collect(Collectors.joining(QUERY_STRING_SEPARATOR));
    }

    private Optional<Set<CurrencyExchangeRateData>> fetchExchangeRatesFromApi(final String url) {
        try {
            CurrencyExchangeRateData[] response = restTemplate
                    .getForEntity(url, CurrencyExchangeRateData[].class)
                    .getBody();

            if (response == null || response.length == 0) {
                return Optional.empty();
            }

            return Optional.of(Set.of(response));
        } catch (final RestClientException exception) {
            log.error("Failed to fetch exchange rates from API. URL: {}", url, exception);
            return Optional.empty();
        }
    }
}