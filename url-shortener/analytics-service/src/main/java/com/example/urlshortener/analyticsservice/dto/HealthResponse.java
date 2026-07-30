package com.example.urlshortener.analyticsservice.dto;

import java.time.Instant;

public record HealthResponse(String status, String serviceName, Instant checkedAt) {
}