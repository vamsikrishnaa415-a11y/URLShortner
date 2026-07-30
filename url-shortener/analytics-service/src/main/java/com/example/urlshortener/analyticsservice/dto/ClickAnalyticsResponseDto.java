package com.example.urlshortener.analyticsservice.dto;

import java.time.Instant;

public record ClickAnalyticsResponseDto(
        Long id,
        String shortCode,
        Instant clickedAt,
        String ipAddress,
        String browser,
        String device,
        String operatingSystem,
        String referrer) {
}