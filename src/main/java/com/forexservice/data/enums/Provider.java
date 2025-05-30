package com.forexservice.data.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {
    CURRENCY_LAYER("currencylayer.com");

    private final String value;
}
