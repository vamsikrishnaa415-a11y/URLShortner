package com.example.urlshortener.orchestratorservice.dto;

import java.time.Instant;

public record ApprovalHistoryDto(
        Long id,
        Long workflowExecutionId,
        String approver,
        String decision,
        String comments,
        Instant decidedAt) {
}