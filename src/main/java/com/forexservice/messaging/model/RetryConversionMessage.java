package com.forexservice.messaging.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetryConversionMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String from;
    private String to;
    private BigDecimal amount;
}
