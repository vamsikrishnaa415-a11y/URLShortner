package com.example.urlshortener.urlservice.dto;

import java.time.Instant;

public record HealthResponse(String status, String serviceName, Instant checkedAt) {
}