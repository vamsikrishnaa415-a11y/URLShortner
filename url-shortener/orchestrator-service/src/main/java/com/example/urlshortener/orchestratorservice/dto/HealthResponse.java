package com.example.urlshortener.orchestratorservice.dto;

import java.time.Instant;

public record HealthResponse(String status, String serviceName, Instant checkedAt) {
}