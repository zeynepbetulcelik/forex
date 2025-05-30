package com.forexservice.impl;

import com.forexservice.client.CurrencyLayerClient;
import com.forexservice.config.CurrencyLayerProperties;
import com.forexservice.data.dto.CurrencyLayerLiveResponseDto;
import com.forexservice.exception.ExchangeRateException;
import com.forexservice.service.impl.ExchangeRateServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceImplTest {

    @Mock
    private CurrencyLayerClient currencyLayerClient;

    @Mock
    private CurrencyLayerProperties currencyLayerProperties;

    @InjectMocks
    private ExchangeRateServiceImpl exchangeRateService;

    @Test
    void givenValidCurrencies_whenGetExchangeRate_thenReturnsExchangeRate() {
        String from = "USD";
        String to = "EUR";
        String accessKey = "dummy-key";
        String key = from + to;

        CurrencyLayerLiveResponseDto responseDto = new CurrencyLayerLiveResponseDto();
        responseDto.setSuccess(true);
        Map<String, Double> quotes = new HashMap<>();
        quotes.put(key, 0.85);
        responseDto.setQuotes(quotes);

        when(currencyLayerProperties.getAccessKey()).thenReturn(accessKey);
        when(currencyLayerClient.getLiveRates(accessKey, from, to)).thenReturn(responseDto);

        BigDecimal result = exchangeRateService.getExchangeRate(from, to);

        assertEquals(BigDecimal.valueOf(0.85), result);
    }

    @Test
    void givenUnsuccessfulResponse_whenGetExchangeRate_thenThrowsExchangeRateException() {
        when(currencyLayerProperties.getAccessKey()).thenReturn("key");
        CurrencyLayerLiveResponseDto response = new CurrencyLayerLiveResponseDto();
        response.setSuccess(false);
        when(currencyLayerClient.getLiveRates(any(), any(), any())).thenReturn(response);

        assertThrows(ExchangeRateException.class, () -> {
            exchangeRateService.getExchangeRate("USD", "EUR");
        });
    }

}