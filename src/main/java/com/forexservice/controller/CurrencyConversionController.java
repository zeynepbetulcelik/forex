package com.forexservice.controller;

import com.forexservice.data.dto.ConversionRequestDto;
import com.forexservice.data.dto.ConversionResponseDto;
import com.forexservice.data.dto.ConversionTransactionDto;
import com.forexservice.data.dto.ExchangeRateResponseDto;
import com.forexservice.data.dto.BulkConversionResponseDto;
import com.forexservice.service.BulkConversionService;
import com.forexservice.service.CurrencyConversionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversions")
@Validated
@RequiredArgsConstructor
public class CurrencyConversionController {

    private final CurrencyConversionService currencyConversionService;
    private final BulkConversionService bulkConversionService;

    @PostMapping
    public ResponseEntity<ConversionResponseDto> convertCurrency(@Valid @RequestBody ConversionRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(currencyConversionService.convertCurrency(requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversionTransactionDto> getConversionById(@PathVariable("id") UUID transactionId) {
        ConversionTransactionDto conversion = currencyConversionService.getConversionById(transactionId);
        return ResponseEntity.ok(conversion);
    }

    @GetMapping
    public ResponseEntity<List<ConversionTransactionDto>> getConversionsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<ConversionTransactionDto> conversions = currencyConversionService.getConversionsByDate(startDate, endDate);
        return ResponseEntity.ok(conversions);
    }

    @GetMapping("/rate")
    public ResponseEntity<ExchangeRateResponseDto> getExchangeRate(
            @RequestParam String from,
            @RequestParam String to) {
        ExchangeRateResponseDto rate = currencyConversionService.getExchangeRate(from, to);
        return ResponseEntity.ok(rate);
    }

    @PostMapping("/bulk")
    public ResponseEntity<BulkConversionResponseDto> bulkConvert(@RequestParam("file") MultipartFile file) {
        BulkConversionResponseDto bulkConversionResponseDto = bulkConversionService.processCsvFile(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(bulkConversionResponseDto);
    }

}
