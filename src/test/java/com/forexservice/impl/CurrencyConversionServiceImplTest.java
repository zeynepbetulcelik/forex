package com.forexservice.impl;

import com.forexservice.data.dto.ConversionRequestDto;
import com.forexservice.data.dto.ConversionResponseDto;
import com.forexservice.data.dto.ConversionTransactionDto;
import com.forexservice.data.dto.ExchangeRateResponseDto;
import com.forexservice.data.entity.CurrencyConversion;
import com.forexservice.exception.ResourceNotFoundException;
import com.forexservice.mapper.ConversionMapper;
import com.forexservice.repository.CurrencyConversionRepository;
import com.forexservice.service.ExchangeRateService;
import com.forexservice.service.impl.CurrencyConversionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyConversionServiceImplTest {

    @Mock
    private CurrencyConversionRepository conversionRepository;

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private ConversionMapper conversionMapper;

    @InjectMocks
    private CurrencyConversionServiceImpl currencyConversionService;

    @Test
    void givenValidRequest_whenConvertCurrency_thenReturnsConvertedAmountAndTransactionId() {
        ConversionRequestDto request = new ConversionRequestDto();
        request.setFrom("USD");
        request.setTo("EUR");
        request.setAmount(BigDecimal.valueOf(100));

        CurrencyConversion conversion = new CurrencyConversion();
        conversion.setId(UUID.randomUUID());
        conversion.setExchangeRate(BigDecimal.valueOf(0.85));
        conversion.setAmount(BigDecimal.valueOf(100));
        conversion.setConvertedAmount(BigDecimal.valueOf(85));
        conversion.setSourceCurrency("USD");
        conversion.setTargetCurrency("EUR");

        when(exchangeRateService.getExchangeRate("USD", "EUR"))
                .thenReturn(BigDecimal.valueOf(0.85));
        when(conversionRepository.saveAndFlush(any(CurrencyConversion.class)))
                .thenReturn(conversion);

        ConversionResponseDto result = currencyConversionService.convertCurrency(request);

        assertEquals(BigDecimal.valueOf(85), result.getConvertedAmount());
        assertEquals(conversion.getId(), result.getTransactionId());
    }

    @Test
    void givenExistingTransactionId_whenGetConversionById_thenReturnsTransactionDto() {
        UUID id = UUID.randomUUID();
        CurrencyConversion entity = new CurrencyConversion();
        entity.setId(id);
        entity.setSourceCurrency("USD");
        entity.setTargetCurrency("EUR");
        entity.setAmount(BigDecimal.valueOf(100));
        entity.setConvertedAmount(BigDecimal.valueOf(85));
        entity.setExchangeRate(BigDecimal.valueOf(0.85));
        entity.setTransactionDate(LocalDateTime.now());

        ConversionTransactionDto dto = ConversionTransactionDto.builder()
                .transactionId(id)
                .fromCurrency("USD")
                .toCurrency("EUR")
                .build();

        when(conversionRepository.findById(id)).thenReturn(Optional.of(entity));
        when(conversionMapper.toDto(entity)).thenReturn(dto);

        ConversionTransactionDto result = currencyConversionService.getConversionById(id);

        assertEquals(id, result.getTransactionId());
        assertEquals("USD", result.getFromCurrency());
    }

    @Test
    void givenInvalidTransactionId_whenGetConversionById_thenThrowsResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(conversionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            currencyConversionService.getConversionById(id);
        });
    }

    @Test
    void givenDateRange_whenGetConversionsByDate_thenReturnsMatchingTransactions() {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();

        CurrencyConversion entity = new CurrencyConversion();
        entity.setId(UUID.randomUUID());
        entity.setSourceCurrency("USD");
        entity.setTargetCurrency("EUR");
        entity.setAmount(BigDecimal.valueOf(100));
        entity.setConvertedAmount(BigDecimal.valueOf(85));
        entity.setExchangeRate(BigDecimal.valueOf(0.85));
        entity.setTransactionDate(LocalDateTime.now().minusDays(1));

        when(conversionRepository.findAllByTransactionDateBetween(start, end))
                .thenReturn(List.of(entity));

        List<ConversionTransactionDto> result = currencyConversionService.getConversionsByDate(start, end);

        assertEquals(1, result.size());
        assertEquals("USD", result.getFirst().getFromCurrency());
    }

    @Test
    void givenValidCurrencies_whenGetExchangeRate_thenReturnsResponseDto() {
        when(exchangeRateService.getExchangeRate("USD", "EUR"))
                .thenReturn(BigDecimal.valueOf(0.85));

        ExchangeRateResponseDto response = currencyConversionService.getExchangeRate("USD", "EUR");

        assertEquals("USD", response.getFromCurrency());
        assertEquals("EUR", response.getToCurrency());
        assertEquals(BigDecimal.valueOf(0.85), response.getExchangeRate());
    }

}