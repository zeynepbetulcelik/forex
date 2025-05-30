package com.forexservice.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CurrencyLayerLiveResponseDto {
    private boolean success;
    private String terms;
    private String privacy;
    private long timestamp;
    private String source;
    @JsonProperty("quotes")
    private Map<String, Double> quotes;
}
