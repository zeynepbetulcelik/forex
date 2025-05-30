package com.forexservice.mapper;

import com.forexservice.data.dto.ConversionTransactionDto;
import com.forexservice.data.entity.CurrencyConversion;
import org.springframework.stereotype.Component;

@Component
public class ConversionMapper {

    public ConversionTransactionDto toDto(CurrencyConversion conversion) {
        return ConversionTransactionDto.builder()
                .transactionId(conversion.getId())
                .fromCurrency(conversion.getSourceCurrency())
                .toCurrency(conversion.getTargetCurrency())
                .amount(conversion.getAmount())
                .convertedAmount(conversion.getConvertedAmount())
                .exchangeRate(conversion.getExchangeRate())
                .transactionDate(conversion.getTransactionDate())
                .build();
    }

}