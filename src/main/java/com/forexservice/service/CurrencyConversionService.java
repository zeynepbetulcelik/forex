package com.forexservice.service;

import com.forexservice.data.dto.ConversionRequestDto;
import com.forexservice.data.dto.ConversionResponseDto;
import com.forexservice.data.dto.ConversionTransactionDto;
import com.forexservice.data.dto.ExchangeRateResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CurrencyConversionService {
    ConversionResponseDto convertCurrency(ConversionRequestDto conversionRequestDto);

    ConversionTransactionDto getConversionById(UUID transactionId);

    List<ConversionTransactionDto> getConversionsByDate(LocalDateTime startDate, LocalDateTime endDate);

    ExchangeRateResponseDto getExchangeRate(String from, String to);
}