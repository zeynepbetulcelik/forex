package com.forexservice.data.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ConversionTransactionDto {
    private UUID transactionId;
    private BigDecimal amount;
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal convertedAmount;
    private BigDecimal exchangeRate;
    private LocalDateTime transactionDate;
}
