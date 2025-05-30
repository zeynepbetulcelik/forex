package com.forexservice.client;

import com.forexservice.data.dto.CurrencyLayerLiveResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "currencyLayerClient", url = "${currencylayer.base-url}")
public interface CurrencyLayerClient {
    @GetMapping("/live")
    CurrencyLayerLiveResponseDto getLiveRates(
            @RequestParam("access_key") String accessKey,
            @RequestParam("source") String sourceCurrency,
            @RequestParam("currencies") String targetCurrencies);
}