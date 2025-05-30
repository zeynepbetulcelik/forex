package com.forexservice.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "currencylayer")
public class CurrencyLayerProperties {

    private String accessKey;
    private String baseUrl;

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

}