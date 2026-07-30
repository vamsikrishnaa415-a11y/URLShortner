package com.example.urlshortener.orchestratorservice.service.engine;

import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class WorkflowStateMachine {

        private final Map<String, Set<String>> transitions = Map.ofEntries(
            Map.entry(WorkflowStates.STARTED, Set.of(WorkflowStates.REQUIREMENT_COMPLETED, WorkflowStates.SAFE_STOPPED)),
            Map.entry(WorkflowStates.REQUIREMENT_COMPLETED, Set.of(WorkflowStates.PLANNING_COMPLETED, WorkflowStates.SAFE_STOPPED)),
            Map.entry(WorkflowStates.PLANNING_COMPLETED, Set.of(WorkflowStates.ARCHITECTURE_COMPLETED, WorkflowStates.SAFE_STOPPED)),
            Map.entry(WorkflowStates.ARCHITECTURE_COMPLETED, Set.of(WorkflowStates.IMPLEMENTATION_COMPLETED, WorkflowStates.SAFE_STOPPED)),
            Map.entry(WorkflowStates.IMPLEMENTATION_COMPLETED, Set.of(WorkflowStates.TESTING_COMPLETED, WorkflowStates.SAFE_STOPPED)),
            Map.entry(WorkflowStates.TESTING_COMPLETED, Set.of(WorkflowStates.DOCUMENTATION_COMPLETED, WorkflowStates.SAFE_STOPPED)),
            Map.entry(WorkflowStates.DOCUMENTATION_COMPLETED, Set.of(WorkflowStates.REVIEW_COMPLETED, WorkflowStates.SAFE_STOPPED)),
            Map.entry(WorkflowStates.REVIEW_COMPLETED, Set.of(WorkflowStates.APPROVAL_PENDING, WorkflowStates.SAFE_STOPPED)),
            Map.entry(WorkflowStates.APPROVAL_PENDING, Set.of(WorkflowStates.APPROVED, WorkflowStates.REPLANNED, WorkflowStates.SAFE_STOPPED, WorkflowStates.ROLLED_BACK)),
            Map.entry(WorkflowStates.REPLANNED, Set.of(WorkflowStates.RETRY_PENDING, WorkflowStates.SAFE_STOPPED, WorkflowStates.ROLLED_BACK)),
            Map.entry(WorkflowStates.SAFE_STOPPED, Set.of(WorkflowStates.RETRY_PENDING, WorkflowStates.ROLLED_BACK)),
            Map.entry(WorkflowStates.RETRY_PENDING, Set.of(WorkflowStates.REQUIREMENT_COMPLETED, WorkflowStates.PLANNING_COMPLETED, WorkflowStates.SAFE_STOPPED, WorkflowStates.ROLLED_BACK)));

    public boolean canTransition(String from, String to) {
        if (from == null) {
            return WorkflowStates.STARTED.equals(to);
        }
        return transitions.getOrDefault(from, Set.of()).contains(to);
    }
}