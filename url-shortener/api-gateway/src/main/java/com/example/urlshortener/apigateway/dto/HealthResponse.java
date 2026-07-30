package com.example.urlshortener.apigateway.dto;

import java.time.Instant;

public record HealthResponse(String status, String serviceName, Instant checkedAt) {
}