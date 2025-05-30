package com.forexservice.messaging.processor;

import com.forexservice.data.dto.ConversionRequestDto;
import com.forexservice.messaging.model.RetryConversionMessage;
import com.forexservice.service.CurrencyConversionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConversionRetryProcessor {

    private final CurrencyConversionService conversionService;
    private final BlockingQueue<RetryConversionMessage> messageQueue = new LinkedBlockingQueue<>();

    public void addMessage(RetryConversionMessage message) {
        messageQueue.add(message);
    }

    @Scheduled(
            fixedRateString = "${retry.processing.rate-ms}",
            initialDelayString = "${retry.processing.initial-delay-ms}"
    )
    public void processNext() {
        RetryConversionMessage message = messageQueue.poll();
        if (message != null) {
            log.info("Processing retry message from memory queue: {}", message);
            try {
                ConversionRequestDto dto = new ConversionRequestDto();
                dto.setFrom(message.getFrom());
                dto.setTo(message.getTo());
                dto.setAmount(message.getAmount());

                conversionService.convertCurrency(dto);
            } catch (Exception e) {
                log.error("Retry processing failed: {}", message, e);
            }
        }
    }

}