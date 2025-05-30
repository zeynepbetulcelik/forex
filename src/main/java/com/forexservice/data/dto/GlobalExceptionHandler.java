package com.forexservice.data.dto;

import com.forexservice.exception.CsvProcessingException;
import com.forexservice.exception.ExchangeRateException;
import com.forexservice.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorDto> handleRuntimeException(RuntimeException e) {
        final ErrorDto err = new ErrorDto(ErrorCode.INTERNAL_SERVER_ERROR.toErrorDetails(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }

    @ExceptionHandler(ExchangeRateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorDto> handleExchangeRateException(ExchangeRateException e) {
        final ErrorDto err = new ErrorDto(ErrorCode.EXCHANGE_RATE_ERROR.toErrorDetails(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorDto> handleResourceNotFound(ResourceNotFoundException e) {
        final ErrorDto err = new ErrorDto(ErrorCode.RESOURCE_NOT_FOUND.toErrorDetails(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ViolationErrorResponse> handleViolationExceptions(MethodArgumentNotValidException e) {
        ViolationErrorResponse response = new ViolationErrorResponse();
        response.setErrorDetails(ErrorCode.VALIDATION_ERROR.toErrorDetails());
        response.setViolations(e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new Violation(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()))
                .toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CsvProcessingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorDto> handleCsvProcessingException(CsvProcessingException e) {
        final ErrorDto err = new ErrorDto(ErrorCode.CSV_PROCESSING_ERROR.toErrorDetails(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @Data
    @AllArgsConstructor
    static class Violation {
        String field;
        String message;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ViolationErrorResponse {
        List<Violation> violations = new ArrayList<>();
        ErrorDetails errorDetails;
    }

}
