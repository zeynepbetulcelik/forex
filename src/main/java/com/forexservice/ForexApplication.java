package com.forexservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFeignClients(basePackages = "com.forexservice.client")
@EnableCaching
@EnableScheduling
@SpringBootApplication
public class ForexApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForexApplication.class, args);
    }

}
