package com.example.urlshortener.orchestratorservice.service.agent;

import java.time.Instant;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.example.urlshortener.orchestratorservice.service.engine.WorkflowStates;

@Component
@Order(4)
public class ImplementationAgent implements WorkflowAgent {

    @Override
    public String name() {
        return "ImplementationAgent";
    }

    @Override
    public List<String> dependencies() {
        return List.of("ArchitectureAgent");
    }

    @Override
    public String completedState() {
        return WorkflowStates.IMPLEMENTATION_COMPLETED;
    }

    @Override
    public void execute(WorkflowRuntimeContext context) {
        context.put("implementation.completedAt", Instant.now().toString());
    }
}