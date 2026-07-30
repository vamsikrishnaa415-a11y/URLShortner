package com.example.urlshortener.orchestratorservice.mapper;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.example.urlshortener.orchestratorservice.dto.ApprovalHistoryDto;
import com.example.urlshortener.orchestratorservice.dto.WorkflowAuditTrailDto;
import com.example.urlshortener.orchestratorservice.dto.WorkflowContextEntryDto;
import com.example.urlshortener.orchestratorservice.dto.WorkflowDetailResponseDto;
import com.example.urlshortener.orchestratorservice.entity.ApprovalHistory;
import com.example.urlshortener.orchestratorservice.entity.WorkflowAuditTrail;
import com.example.urlshortener.orchestratorservice.entity.WorkflowContextEntry;
import com.example.urlshortener.orchestratorservice.entity.WorkflowExecution;

@Component
public class WorkflowOrchestrationMapper {

    public WorkflowDetailResponseDto toDetailDto(
            WorkflowExecution execution,
            Map<String, List<String>> dependencyGraph,
            List<WorkflowContextEntry> contextEntries,
            List<ApprovalHistory> decisions,
            List<WorkflowAuditTrail> auditTrail) {
        return new WorkflowDetailResponseDto(
                execution.getId(),
                execution.getWorkflowName(),
                execution.getCorrelationId(),
                execution.getStartedAt(),
                execution.getCompletedAt(),
                execution.getInitiatedBy(),
                execution.getWorkflowState().getStateKey(),
                dependencyGraph,
                contextEntries.stream().map(this::toContextDto).toList(),
                decisions.stream().map(this::toDecisionDto).toList(),
                auditTrail.stream().map(this::toAuditDto).toList());
    }

    private WorkflowContextEntryDto toContextDto(WorkflowContextEntry entry) {
        return new WorkflowContextEntryDto(entry.getContextKey(), entry.getContextValue(), entry.getUpdatedAt());
    }

    private ApprovalHistoryDto toDecisionDto(ApprovalHistory history) {
        return new ApprovalHistoryDto(
                history.getId(),
                history.getWorkflowExecution().getId(),
                history.getApprover(),
                history.getDecision(),
                history.getComments(),
                history.getDecidedAt());
    }

    private WorkflowAuditTrailDto toAuditDto(WorkflowAuditTrail trail) {
        return new WorkflowAuditTrailDto(trail.getAction(), trail.getDetails(), trail.getCreatedAt());
    }
}