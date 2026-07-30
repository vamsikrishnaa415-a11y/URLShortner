package com.example.urlshortener.orchestratorservice.dto;

import java.time.Instant;

public record WorkflowContextEntryDto(String key, String value, Instant updatedAt) {
}