package com.example.urlshortener.urlservice.dto;

import java.time.Instant;

public record AnalyticsEventRequestDto(
        String shortCode,
        Instant clickedAt,
        String ipAddress,
        String browser,
        String device,
        String operatingSystem,
        String referrer) {
}