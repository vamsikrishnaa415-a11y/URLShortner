package com.example.urlshortener.orchestratorservice.repository;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import com.example.urlshortener.orchestratorservice.entity.WorkflowExecution;
import com.example.urlshortener.orchestratorservice.entity.WorkflowState;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class WorkflowExecutionRepositoryTest {

    @Autowired
    private WorkflowExecutionRepository workflowExecutionRepository;

    @Autowired
    private WorkflowStateRepository workflowStateRepository;

    @Test
    void shouldSaveAndFindByCorrelationId() {
        WorkflowState state = new WorkflowState();
        state.setStateKey("RUNNING");
        state.setDescription("Workflow running");
        state.setTerminal(false);
        WorkflowState savedState = workflowStateRepository.save(state);

        WorkflowExecution execution = new WorkflowExecution();
        execution.setWorkflowName("ShortUrlCreation");
        execution.setCorrelationId("corr-001");
        execution.setStartedAt(Instant.now());
        execution.setInitiatedBy("system");
        execution.setWorkflowState(savedState);

        workflowExecutionRepository.save(execution);

        Optional<WorkflowExecution> found = workflowExecutionRepository.findByCorrelationId("corr-001");
        assertThat(found).isPresent();
        assertThat(found.get().getWorkflowName()).isEqualTo("ShortUrlCreation");
    }
}