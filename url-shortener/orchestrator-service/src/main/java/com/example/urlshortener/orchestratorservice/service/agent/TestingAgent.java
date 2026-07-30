package com.example.urlshortener.orchestratorservice.service.agent;

import java.time.Instant;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.example.urlshortener.orchestratorservice.service.engine.WorkflowStates;

@Component
@Order(5)
public class TestingAgent implements WorkflowAgent {

    @Override
    public String name() {
        return "TestingAgent";
    }

    @Override
    public List<String> dependencies() {
        return List.of("ImplementationAgent");
    }

    @Override
    public String completedState() {
        return WorkflowStates.TESTING_COMPLETED;
    }

    @Override
    public void execute(WorkflowRuntimeContext context) {
        context.put("testing.completedAt", Instant.now().toString());
    }
}