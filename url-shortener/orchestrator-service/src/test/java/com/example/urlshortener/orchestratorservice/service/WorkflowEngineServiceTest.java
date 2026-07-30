package com.example.urlshortener.orchestratorservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import com.example.urlshortener.orchestratorservice.dto.WorkflowActionRequestDto;
import com.example.urlshortener.orchestratorservice.dto.WorkflowActionResponseDto;
import com.example.urlshortener.orchestratorservice.dto.WorkflowApprovalRequestDto;
import com.example.urlshortener.orchestratorservice.dto.WorkflowDetailResponseDto;
import com.example.urlshortener.orchestratorservice.dto.WorkflowStartRequestDto;
import com.example.urlshortener.orchestratorservice.service.engine.WorkflowStates;

@SpringBootTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class WorkflowEngineServiceTest {

    @Autowired
    private WorkflowEngineService workflowEngineService;

    @Test
    void shouldStartWorkflowAndReachApprovalGate() {
        WorkflowStartRequestDto request = new WorkflowStartRequestDto(
                "SDLC-Flow",
                "architect",
                "corr-start-1",
                Map.of("ticket", "URL-101"));

        WorkflowActionResponseDto response = workflowEngineService.startWorkflow(request);

        assertThat(response.workflowId()).isNotNull();
        assertThat(response.state()).isEqualTo(WorkflowStates.APPROVAL_PENDING);

        WorkflowDetailResponseDto detail = workflowEngineService.getWorkflow(response.workflowId());
        assertThat(detail.contextEntries()).isNotEmpty();
        assertThat(detail.dependencyGraph()).containsKey("RequirementAgent");
        assertThat(detail.auditTrail()).isNotEmpty();
    }

    @Test
    void shouldApproveWorkflow() {
        WorkflowActionResponseDto started = workflowEngineService.startWorkflow(new WorkflowStartRequestDto(
                "SDLC-Approve",
                "lead",
                "corr-approve-1",
                Map.of()));

        WorkflowActionResponseDto approved = workflowEngineService.approveWorkflow(
                started.workflowId(),
                new WorkflowApprovalRequestDto("reviewer", "APPROVE", "Approved"));

        assertThat(approved.state()).isEqualTo(WorkflowStates.APPROVED);

        WorkflowDetailResponseDto detail = workflowEngineService.getWorkflow(started.workflowId());
        assertThat(detail.currentState()).isEqualTo(WorkflowStates.APPROVED);
        assertThat(detail.decisionHistory()).hasSize(1);
    }

    @Test
    void shouldReplanOnRejectAndRetryToApprovalGate() {
        WorkflowActionResponseDto started = workflowEngineService.startWorkflow(new WorkflowStartRequestDto(
                "SDLC-Replan",
                "lead",
                "corr-replan-1",
                Map.of()));

        WorkflowActionResponseDto replanned = workflowEngineService.approveWorkflow(
                started.workflowId(),
                new WorkflowApprovalRequestDto("reviewer", "REJECT", "Need updates"));

        assertThat(replanned.state()).isEqualTo(WorkflowStates.REPLANNED);

        WorkflowActionResponseDto retried = workflowEngineService.retryWorkflow(
                started.workflowId(),
                new WorkflowActionRequestDto("Replanning completed"));

        assertThat(retried.state()).isEqualTo(WorkflowStates.APPROVAL_PENDING);
    }

    @Test
    void shouldRollbackWorkflow() {
        WorkflowActionResponseDto started = workflowEngineService.startWorkflow(new WorkflowStartRequestDto(
                "SDLC-Rollback",
                "ops",
                "corr-rollback-1",
                Map.of()));

        WorkflowActionResponseDto rolledBack = workflowEngineService.rollbackWorkflow(
                started.workflowId(),
                new WorkflowActionRequestDto("Manual rollback"));

        assertThat(rolledBack.state()).isEqualTo(WorkflowStates.ROLLED_BACK);
    }
}