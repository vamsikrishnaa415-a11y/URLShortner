package com.example.urlshortener.urlservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.urlshortener.urlservice.dto.AnalyticsEventRequestDto;

@FeignClient(
        name = "analytics-service",
        url = "${analytics.service.base-url}",
        configuration = AnalyticsServiceFeignConfig.class,
        fallbackFactory = AnalyticsServiceClientFallbackFactory.class)
public interface AnalyticsServiceClient {

    @PostMapping("/analytics/events")
    void storeRedirectEvent(@RequestBody AnalyticsEventRequestDto request);
}