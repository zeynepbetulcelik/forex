package com.forexservice.messaging.consumer;

import com.forexservice.messaging.model.RetryConversionMessage;
import com.forexservice.messaging.processor.ConversionRetryProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConversionRetryConsumer {

    private final ConversionRetryProcessor processor;

    @RabbitListener(queues = "conversion-retry-queue")
    public void listen(RetryConversionMessage message) {
        log.info("Received retry message from RabbitMQ: {}", message);
        processor.addMessage(message);
    }
}

