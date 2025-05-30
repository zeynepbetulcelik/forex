package com.forexservice.service.impl;

import com.forexservice.data.dto.ConversionRequestDto;
import com.forexservice.data.dto.ConversionResponseDto;
import com.forexservice.data.dto.ConversionTransactionDto;
import com.forexservice.data.dto.ExchangeRateResponseDto;
import com.forexservice.data.entity.CurrencyConversion;
import com.forexservice.data.enums.Provider;
import com.forexservice.exception.ResourceNotFoundException;
import com.forexservice.mapper.ConversionMapper;
import com.forexservice.repository.CurrencyConversionRepository;
import com.forexservice.service.CurrencyConversionService;
import com.forexservice.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    private final CurrencyConversionRepository conversionRepository;
    private final ExchangeRateService exchangeRateService;
    private final ConversionMapper conversionMapper;

    @Override
    public ConversionResponseDto convertCurrency(ConversionRequestDto conversionRequestDto) {
        String fromCurrency = conversionRequestDto.getFrom();
        String toCurrency = conversionRequestDto.getTo();
        BigDecimal amount = conversionRequestDto.getAmount();
        BigDecimal exchangeRate = exchangeRateService.getExchangeRate(fromCurrency, toCurrency);
        BigDecimal convertedAmount = amount.multiply(exchangeRate).setScale(4, RoundingMode.HALF_UP);

        CurrencyConversion conversion = new CurrencyConversion();
        conversion.setSourceCurrency(fromCurrency);
        conversion.setTargetCurrency(toCurrency);
        conversion.setAmount(amount);
        conversion.setConvertedAmount(convertedAmount);
        conversion.setExchangeRate(exchangeRate);

        CurrencyConversion savedCurrencyConversion = conversionRepository.saveAndFlush(conversion);

        return new ConversionResponseDto(savedCurrencyConversion.getConvertedAmount(), savedCurrencyConversion.getId());
    }

    @Override
    public ConversionTransactionDto getConversionById(UUID transactionId) {
        return conversionRepository.findById(transactionId)
                .map(conversionMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));
    }

    @Override
    public List<ConversionTransactionDto> getConversionsByDate(LocalDateTime startDate, LocalDateTime endDate) {
        return conversionRepository.findAllByTransactionDateBetween(startDate, endDate)
                .stream()
                .map(conversion -> ConversionTransactionDto.builder()
                        .transactionId(conversion.getId())
                        .fromCurrency(conversion.getSourceCurrency())
                        .toCurrency(conversion.getTargetCurrency())
                        .amount(conversion.getAmount())
                        .convertedAmount(conversion.getConvertedAmount())
                        .exchangeRate(conversion.getExchangeRate())
                        .transactionDate(conversion.getTransactionDate())
                        .build()).toList();
    }

    @Override
    public ExchangeRateResponseDto getExchangeRate(String from, String to) {
        BigDecimal rate = exchangeRateService.getExchangeRate(from, to);
        return ExchangeRateResponseDto.builder()
                .fromCurrency(from.toUpperCase())
                .toCurrency(to.toUpperCase())
                .exchangeRate(rate)
                .provider(Provider.CURRENCY_LAYER.getValue())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

}
