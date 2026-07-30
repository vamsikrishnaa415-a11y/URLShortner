package com.example.urlshortener.orchestratorservice.service.agent;

import java.time.Instant;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.example.urlshortener.orchestratorservice.service.engine.WorkflowStates;

@Component
@Order(3)
public class ArchitectureAgent implements WorkflowAgent {

    @Override
    public String name() {
        return "ArchitectureAgent";
    }

    @Override
    public List<String> dependencies() {
        return List.of("PlanningAgent");
    }

    @Override
    public String completedState() {
        return WorkflowStates.ARCHITECTURE_COMPLETED;
    }

    @Override
    public void execute(WorkflowRuntimeContext context) {
        context.put("architecture.completedAt", Instant.now().toString());
    }
}