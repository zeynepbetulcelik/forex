package com.forexservice.impl;

import com.forexservice.data.dto.ConversionResponseDto;
import com.forexservice.exception.CsvProcessingException;
import com.forexservice.exception.ExchangeRateException;
import com.forexservice.messaging.model.RetryConversionMessage;
import com.forexservice.messaging.producer.ConversionRetryProducer;
import com.forexservice.service.CurrencyConversionService;
import com.forexservice.service.impl.BulkConversionServiceImpl;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;


class BulkConversionServiceImplTest {

    @Mock
    private CurrencyConversionService conversionService;

    @Mock
    private ConversionRetryProducer conversionRetryProducer;

    @InjectMocks
    private BulkConversionServiceImpl bulkConversionService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenValidCsvWithOneFailingRecord_whenProcessed_thenRetriesAndReturnsPartialSuccess() throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(out), CSVFormat.DEFAULT
                .builder()
                .setHeader("from", "to", "amount")
                .build());
        printer.printRecord("USD", "EUR", "100");
        printer.printRecord("GBP", "USD", "50");
        printer.flush();

        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", out.toByteArray());

        when(conversionService.convertCurrency(argThat(req -> req != null && "USD".equals(req.getFrom()))))
                .thenReturn(new ConversionResponseDto(new BigDecimal("87.50"), UUID.randomUUID()));

        when(conversionService.convertCurrency(argThat(req -> req != null && "GBP".equals(req.getFrom()))))
                .thenThrow(new ExchangeRateException("rate limit"));

        var result = bulkConversionService.processCsvFile(file);

        assertEquals(1, result.getSuccessfulConversions().size());
        assertEquals(1, result.getQueuedForRetry().size());

        verify(conversionRetryProducer, times(1)).sendToQueue(any(RetryConversionMessage.class));
    }

    @Test
    void givenMalformedCsv_whenProcessed_thenThrowsCsvProcessingException() {
        String badCsv = "from,amount\nUSD,100";
        MockMultipartFile file = new MockMultipartFile("file", "bad.csv", "text/csv", badCsv.getBytes());

        assertThrows(CsvProcessingException.class, () -> bulkConversionService.processCsvFile(file));
    }

    @Test
    void givenEmptyCsv_whenProcessed_thenReturnsEmptyResponse() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(out), CSVFormat.DEFAULT
                .builder()
                .setHeader("from", "to", "amount")
                .build());
        printer.flush();

        MockMultipartFile file = new MockMultipartFile("file", "empty.csv", "text/csv", out.toByteArray());

        var result = bulkConversionService.processCsvFile(file);

        assertTrue(result.getSuccessfulConversions().isEmpty());
        assertTrue(result.getQueuedForRetry().isEmpty());
    }

    @Test
    void givenValidCsvWithAllRecordsFailing_whenProcessed_thenRetriesAllAndReturnsEmptySuccess() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(out), CSVFormat.DEFAULT
                .builder()
                .setHeader("from", "to", "amount")
                .build());
        printer.printRecord("USD", "EUR", "100");
        printer.printRecord("GBP", "USD", "50");
        printer.flush();

        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", out.toByteArray());

        when(conversionService.convertCurrency(any()))
                .thenThrow(new ExchangeRateException("rate limit"));

        var result = bulkConversionService.processCsvFile(file);

        assertTrue(result.getSuccessfulConversions().isEmpty());
        assertEquals(2, result.getQueuedForRetry().size());

        verify(conversionRetryProducer, times(2)).sendToQueue(any(RetryConversionMessage.class));
    }

}