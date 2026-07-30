package com.example.urlshortener.urlservice.client;

import feign.Request;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;

public class AnalyticsServiceFeignConfig {

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100, TimeUnit.SECONDS.toMillis(1), 3);
    }

    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(2, TimeUnit.SECONDS, 3, TimeUnit.SECONDS, true);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new AnalyticsServiceErrorDecoder();
    }
}