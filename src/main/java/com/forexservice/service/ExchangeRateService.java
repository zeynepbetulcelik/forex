package com.forexservice.service;

import java.math.BigDecimal;

public interface ExchangeRateService {
    BigDecimal getExchangeRate(String fromCurrency, String toCurrency);
}
