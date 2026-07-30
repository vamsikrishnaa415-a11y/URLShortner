package com.example.urlshortener.orchestratorservice.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record WorkflowDetailResponseDto(
        Long id,
        String workflowName,
        String correlationId,
        Instant startedAt,
        Instant completedAt,
        String initiatedBy,
        String currentState,
        Map<String, List<String>> dependencyGraph,
        List<WorkflowContextEntryDto> contextEntries,
        List<ApprovalHistoryDto> decisionHistory,
        List<WorkflowAuditTrailDto> auditTrail) {
}