package com.vednovak.manager.currency.services.impl;

import com.vednovak.manager.currency.data.dtos.CurrencyExchangeRateData;
import com.vednovak.manager.currency.exceptions.CurrencyExchangeRateException;
import com.vednovak.manager.currency.services.CurrencyExchangeRateService;
import com.vednovak.manager.message.services.MessageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.vednovak.manager.currency.utils.CurrencyConstants.*;

@Slf4j
@Service
@PropertySource("classpath:currencies.properties")
public class DefaultCurrencyExchangeRateService implements CurrencyExchangeRateService {

    private final Map<String, BigDecimal> exchangeRates;
    private final RestTemplate restTemplate;
    private final MessageService messageService;
    private final Set<String> supportedCurrencies;
    private final String exchangeRateBaseApiUrl;
    private final String queryCurrencyForExchangeRateUrl;
    private final String queryDateForExchangeRateUrl;
    private final String dateFormatForExchangeRateUrl;

    public DefaultCurrencyExchangeRateService(
            final RestTemplate restTemplate,
            final MessageService messageService,
            @Value("${supported.currencies}") final Set<String> supportedCurrencies,
            @Value("${exchange.rate.base.api.url}") final String exchangeRateBaseApiUrl,
            @Value("${exchange.rate.currency.query.api.url}") final String queryCurrencyForExchangeRateUrl,
            @Value("${exchange.rate.date.query.api.url}") final String queryDateForExchangeRateUrl,
            @Value("${exchange.rate.api.url.date.format}") final String dateFormatForExchangeRateUrl) {
        this.restTemplate = restTemplate;
        this.messageService = messageService;
        this.supportedCurrencies = supportedCurrencies;
        this.exchangeRateBaseApiUrl = exchangeRateBaseApiUrl;
        this.queryCurrencyForExchangeRateUrl = queryCurrencyForExchangeRateUrl;
        this.queryDateForExchangeRateUrl = queryDateForExchangeRateUrl;
        this.dateFormatForExchangeRateUrl = dateFormatForExchangeRateUrl;
        this.exchangeRates = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void initializeExchangeRates() {
        updateExchangeRates();
    }

    @Override
    public void updateExchangeRates() {
        try {
            final Set<CurrencyExchangeRateData> fetchedExchangeRates = fetchExchangeRates();
            validateFetchedExchangeRatesOrElseThrow(fetchedExchangeRates);
            removeNoLongerSupportedExchangeRates();
            fetchedExchangeRates.forEach(this::saveExchangeRate);
        } catch (Exception ex) {
            log.error("Unexpected error during exchange rate update", ex);
            throw new CurrencyExchangeRateException(messageService.getMessage(ERROR_UNEXPECTED_EXCHANGE_RATE_UPDATE));
        }
    }

    private Set<CurrencyExchangeRateData> fetchExchangeRates() {
        final String url = buildApiUrl();
        return fetchExchangeRatesFromApi(url)
                .orElseThrow(() ->
                        new CurrencyExchangeRateException(messageService.getMessage(ERROR_CURRENCY_NOT_SUPPORTED))
                );
    }

    private void validateFetchedExchangeRatesOrElseThrow(Set<CurrencyExchangeRateData> fetchedExchangeRates) {
        if (fetchedExchangeRates == null || fetchedExchangeRates.isEmpty()
                || fetchedExchangeRates.size() != supportedCurrencies.size()
                || !fetchedExchangeRates.stream().map(CurrencyExchangeRateData::getCurrency).collect(Collectors.toSet())
                .containsAll(supportedCurrencies)) {
            log.error("Invalid fetched exchange rates or supported currencies.");
            throw new CurrencyExchangeRateException(messageService.getMessage(ERROR_INVALID_EXCHANGE_RATE_DATA));
        }
    }

    private String buildApiUrl() {
        return StringUtils.join(
                exchangeRateBaseApiUrl, QUERY_STRING_QUESTION_MARK,
                buildDateQuery(), QUERY_STRING_SEPARATOR,
                buildCurrencyQuery());
    }

    private String buildDateQuery() {
        final String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern(dateFormatForExchangeRateUrl));
        return StringUtils.join(queryDateForExchangeRateUrl, currentDate);
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
        } catch (RestClientException exception) {
            log.error("Failed to fetch exchange rates from API. URL: {}, Error: {}", url, exception.getMessage(),
                    exception);
            return Optional.empty();
        }
    }

    private void removeNoLongerSupportedExchangeRates() {
        exchangeRates.keySet().removeIf(currency -> !supportedCurrencies.contains(currency));
    }

    private void saveExchangeRate(final CurrencyExchangeRateData exchangeRate) {
        final String currency = exchangeRate.getCurrency();
        final BigDecimal sellingRate = parseSellingRate(exchangeRate.getSellingRate());
        exchangeRates.put(currency, sellingRate);
    }

    private BigDecimal parseSellingRate(final String currencySellingRate) {
        final String normalizedRate = StringUtils.replace(currencySellingRate, RADIX_CHARACTER_SEARCH, RADIX_CHARACTER_REPLACEMENT);
        return new BigDecimal(normalizedRate);
    }

    @Override
    public BigDecimal convertPriceForCurrency(final BigDecimal basePrice, final String currency) {
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new CurrencyExchangeRateException(messageService.getMessage(ERROR_INVALID_BASE_PRICE));
        }

        if (!supportedCurrencies.contains(currency)) {
            throw new CurrencyExchangeRateException(messageService.getMessage(ERROR_CURRENCY_NOT_SUPPORTED));
        }

        BigDecimal exchangeRate = Optional
                .ofNullable(exchangeRates.get(currency))
                .orElseThrow(() ->
                        new CurrencyExchangeRateException(messageService.getMessage(ERROR_EXCHANGE_RATE_NOT_FOUND, currency))
                );

        return basePrice.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
    }
}
