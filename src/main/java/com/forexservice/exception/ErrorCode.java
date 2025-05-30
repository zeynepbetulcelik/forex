package com.forexservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    VALIDATION_ERROR("VALIDATION_ERROR", 1001),
    EXCHANGE_RATE_ERROR("EXCHANGE_RATE_ERROR", 1002),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", 1003),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", 1004),
    CSV_PROCESSING_ERROR("CSV_PROCESSING_ERROR", 1005);

    private final String message;
    private final int code;

    public ErrorDetails toErrorDetails() {
        return new ErrorDetails(code, message);
    }
}

