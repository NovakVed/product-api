package com.vednovak.manager.currency.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CurrenciesConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
