package com.example.urlshortener.analyticsservice.dto;

import java.time.Instant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ClickAnalyticsRequestDto(
        @NotBlank @Size(max = 32) String shortCode,
        @NotNull Instant clickedAt,
        @NotBlank @Size(max = 45) String ipAddress,
        @NotBlank @Size(max = 128) String browser,
        @NotBlank @Size(max = 128) String device,
        @NotBlank @Size(max = 128) String operatingSystem,
        @Size(max = 512) String referrer) {
}