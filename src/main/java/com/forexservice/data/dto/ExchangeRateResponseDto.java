package com.forexservice.data.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ExchangeRateResponseDto {
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal exchangeRate;
    private String provider;
    private String timestamp;
}
