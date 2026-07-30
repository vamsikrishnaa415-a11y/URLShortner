package com.example.urlshortener.urlservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.example.urlshortener.urlservice.dto.AnalyticsEventRequestDto;

@Component
public class AnalyticsServiceClientFallbackFactory implements FallbackFactory<AnalyticsServiceClient> {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsServiceClientFallbackFactory.class);

    @Override
    public AnalyticsServiceClient create(Throwable cause) {
        return new AnalyticsServiceClient() {
            @Override
            public void storeRedirectEvent(AnalyticsEventRequestDto request) {
                logger.warn("Fallback activated for analytics event shortCode={}, reason={}", request.shortCode(), cause.toString());
            }
        };
    }
}