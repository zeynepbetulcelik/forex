package com.forexservice.messaging.producer;

import com.forexservice.messaging.model.RetryConversionMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConversionRetryProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendToQueue(RetryConversionMessage message) {
        log.info("Sending message to retry-queue: {}", message);
        rabbitTemplate.convertAndSend("conversion-exchange", "conversion.retry", message);
    }

}
