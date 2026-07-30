package com.example.urlshortener.orchestratorservice.repository;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import com.example.urlshortener.orchestratorservice.entity.WorkflowState;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class WorkflowStateRepositoryTest {

    @Autowired
    private WorkflowStateRepository workflowStateRepository;

    @Test
    void shouldSaveAndFindByStateKey() {
        WorkflowState state = new WorkflowState();
        state.setStateKey("PENDING_APPROVAL");
        state.setDescription("Pending human approval");
        state.setTerminal(false);

        workflowStateRepository.save(state);

        Optional<WorkflowState> found = workflowStateRepository.findByStateKey("PENDING_APPROVAL");
        assertThat(found).isPresent();
        assertThat(found.get().getTerminal()).isFalse();
    }
}