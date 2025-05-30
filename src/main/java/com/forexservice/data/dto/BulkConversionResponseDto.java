package com.forexservice.data.dto;

import com.forexservice.messaging.model.RetryConversionMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkConversionResponseDto {
    private List<ConversionResponseDto> successfulConversions;
    private List<RetryConversionMessage> queuedForRetry;
}