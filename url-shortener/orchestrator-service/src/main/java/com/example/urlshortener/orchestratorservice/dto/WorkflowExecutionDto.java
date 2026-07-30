package com.example.urlshortener.orchestratorservice.dto;

import java.time.Instant;

public record WorkflowExecutionDto(
        Long id,
        String workflowName,
        String correlationId,
        Instant startedAt,
        Instant completedAt,
        String initiatedBy,
        Long workflowStateId) {
}