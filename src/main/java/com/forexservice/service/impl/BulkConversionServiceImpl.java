package com.forexservice.service.impl;

import com.forexservice.data.dto.BulkConversionResponseDto;
import com.forexservice.data.dto.ConversionRequestDto;
import com.forexservice.data.dto.ConversionResponseDto;
import com.forexservice.exception.CsvProcessingException;
import com.forexservice.exception.ExchangeRateException;
import com.forexservice.messaging.model.RetryConversionMessage;
import com.forexservice.messaging.producer.ConversionRetryProducer;
import com.forexservice.service.BulkConversionService;
import com.forexservice.service.CurrencyConversionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class BulkConversionServiceImpl implements BulkConversionService {

    private final CurrencyConversionService conversionService;
    private final ConversionRetryProducer conversionRetryProducer;
    private static final int CHUNK_SIZE = 10;

    @Override
    public BulkConversionResponseDto processCsvFile(MultipartFile file) {
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser parser = new CSVParser(reader, format) ) {

            List<ConversionRequestDto> conversionRequestDtoList = StreamSupport.stream(parser.spliterator(), false)
                    .map(csvRecord -> {
                ConversionRequestDto dto = new ConversionRequestDto();
                dto.setFrom(csvRecord.get("from"));
                dto.setTo(csvRecord.get("to"));
                dto.setAmount(new BigDecimal(csvRecord.get("amount")));
                return dto;
            }).toList();

            List<ConversionResponseDto> successfulConversions = new ArrayList<>();
            List<RetryConversionMessage> queuedForRetry = new ArrayList<>();

            processChunks(conversionRequestDtoList, successfulConversions, queuedForRetry);

            return BulkConversionResponseDto.builder()
                    .successfulConversions(successfulConversions)
                    .queuedForRetry(queuedForRetry)
                    .build();
        } catch (Exception e ) {
            log.error("Error processing CSV file: {}", e.getMessage(), e);
            throw new CsvProcessingException("CSV processing failed");
        }
    }

    private void processChunks(List<ConversionRequestDto> conversionRequestDtoList,
                               List<ConversionResponseDto> successfulConversions,
                               List<RetryConversionMessage> queuedForRetry) {
        for (int i = 0; i < conversionRequestDtoList.size(); i += CHUNK_SIZE) {
            List<ConversionRequestDto> chunk = conversionRequestDtoList.subList(i, Math.min(i + CHUNK_SIZE, conversionRequestDtoList.size()));
            for (ConversionRequestDto request : chunk) {
                try {
                    ConversionResponseDto response = conversionService.convertCurrency(request);
                    successfulConversions.add(response);
                } catch (ExchangeRateException e) {
                    log.warn("Rate limit or exchange rate error occurred, sending to retry queue: {}", request);
                    RetryConversionMessage retryMsg = new RetryConversionMessage(
                            request.getFrom(),
                            request.getTo(),
                            request.getAmount()
                    );
                    conversionRetryProducer.sendToQueue(retryMsg);
                    queuedForRetry.add(retryMsg);
                } catch (Exception e) {
                    log.error("Unexpected error during conversion for request: {}", request, e);
                }
            }
        }
    }

}