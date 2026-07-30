package com.example.urlshortener.orchestratorservice.repository;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import com.example.urlshortener.orchestratorservice.entity.ApprovalHistory;
import com.example.urlshortener.orchestratorservice.entity.WorkflowExecution;
import com.example.urlshortener.orchestratorservice.entity.WorkflowState;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class ApprovalHistoryRepositoryTest {

    @Autowired
    private ApprovalHistoryRepository approvalHistoryRepository;

    @Autowired
    private WorkflowExecutionRepository workflowExecutionRepository;

    @Autowired
    private WorkflowStateRepository workflowStateRepository;

    @Test
    void shouldSaveAndQueryApprovalHistoryByWorkflowExecution() {
        WorkflowState state = new WorkflowState();
        state.setStateKey("COMPLETED");
        state.setDescription("Workflow completed");
        state.setTerminal(true);
        WorkflowState savedState = workflowStateRepository.save(state);

        WorkflowExecution execution = new WorkflowExecution();
        execution.setWorkflowName("ApprovalFlow");
        execution.setCorrelationId("corr-approval-1");
        execution.setStartedAt(Instant.now().minusSeconds(120));
        execution.setCompletedAt(Instant.now().minusSeconds(60));
        execution.setInitiatedBy("orchestrator");
        execution.setWorkflowState(savedState);
        WorkflowExecution savedExecution = workflowExecutionRepository.save(execution);

        ApprovalHistory first = new ApprovalHistory();
        first.setWorkflowExecution(savedExecution);
        first.setApprover("alice");
        first.setDecision("APPROVED");
        first.setComments("Looks good");
        first.setDecidedAt(Instant.now().minusSeconds(30));

        ApprovalHistory second = new ApprovalHistory();
        second.setWorkflowExecution(savedExecution);
        second.setApprover("bob");
        second.setDecision("APPROVED");
        second.setComments("Validated");
        second.setDecidedAt(Instant.now().minusSeconds(10));

        approvalHistoryRepository.save(first);
        approvalHistoryRepository.save(second);

        List<ApprovalHistory> results = approvalHistoryRepository
                .findByWorkflowExecutionIdOrderByDecidedAtAsc(savedExecution.getId());

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getApprover()).isEqualTo("alice");
        assertThat(results.get(1).getApprover()).isEqualTo("bob");
    }
}