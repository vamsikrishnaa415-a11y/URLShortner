package com.example.urlshortener.orchestratorservice.dto;

import java.time.Instant;

public record WorkflowAuditTrailDto(String action, String details, Instant createdAt) {
}