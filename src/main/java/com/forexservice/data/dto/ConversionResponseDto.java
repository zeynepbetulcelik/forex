package com.forexservice.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ConversionResponseDto {
    private BigDecimal convertedAmount;
    private UUID transactionId;
}
