package com.forexservice.service.impl;

import com.forexservice.client.CurrencyLayerClient;
import com.forexservice.config.CurrencyLayerProperties;
import com.forexservice.data.dto.CurrencyLayerLiveResponseDto;
import com.forexservice.exception.ExchangeRateException;
import com.forexservice.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final CurrencyLayerProperties currencyLayerProperties;
    private final CurrencyLayerClient currencyLayerClient;

    @Cacheable(value = "exchangeRates", key = "#fromCurrency + '-' + #toCurrency")
    @Override
    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        try {
            CurrencyLayerLiveResponseDto response = currencyLayerClient.getLiveRates(
                    currencyLayerProperties.getAccessKey(),
                    fromCurrency,
                    toCurrency);

            if (!response.isSuccess()) {
                throw new ExchangeRateException("CurrencyLayer API call unsuccessful");
            }

            String key = fromCurrency + toCurrency;
            Double rate = response.getQuotes().get(key);
            if (rate == null) {
                throw new ExchangeRateException("Exchange rate not found for " + key);
            }
            return BigDecimal.valueOf(rate);

        } catch (Exception e) {
            log.error("Error fetching exchange rate from CurrencyLayer: {}", e.getMessage(), e);
            throw new ExchangeRateException("Failed to get exchange rate");
        }
    }

}
