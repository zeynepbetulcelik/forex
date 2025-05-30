package com.forexservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI forexServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Forex Service API")
                        .description("API for currency conversion and exchange rate retrieval")
                        .version("1.0.0"));
    }
}
