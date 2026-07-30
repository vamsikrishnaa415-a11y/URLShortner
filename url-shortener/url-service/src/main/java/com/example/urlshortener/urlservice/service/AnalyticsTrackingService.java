package com.example.urlshortener.urlservice.service;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.urlshortener.urlservice.client.AnalyticsServiceClient;
import com.example.urlshortener.urlservice.dto.AnalyticsEventRequestDto;
import com.example.urlshortener.urlservice.dto.RedirectMetadataDto;
import com.example.urlshortener.urlservice.util.UserAgentParser;

@Service
public class AnalyticsTrackingService {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsTrackingService.class);

    private final AnalyticsServiceClient analyticsServiceClient;

    public AnalyticsTrackingService(AnalyticsServiceClient analyticsServiceClient) {
        this.analyticsServiceClient = analyticsServiceClient;
    }

    public void trackRedirect(String shortCode, RedirectMetadataDto metadata) {
        String userAgent = metadata == null ? null : metadata.userAgent();
        AnalyticsEventRequestDto request = new AnalyticsEventRequestDto(
                shortCode,
                Instant.now(),
                metadata == null ? "unknown" : safe(metadata.ipAddress(), "unknown"),
                UserAgentParser.detectBrowser(userAgent),
                UserAgentParser.detectDevice(userAgent),
                UserAgentParser.detectOperatingSystem(userAgent),
                metadata == null ? null : metadata.referrer());

        try {
            analyticsServiceClient.storeRedirectEvent(request);
        } catch (Exception ex) {
            logger.warn("Failed to record analytics for shortCode={}, continuing redirect path. reason={}", shortCode, ex.toString());
        }
    }

    private String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}