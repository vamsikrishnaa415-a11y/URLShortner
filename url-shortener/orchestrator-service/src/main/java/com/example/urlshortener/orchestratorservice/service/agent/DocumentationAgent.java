package com.example.urlshortener.orchestratorservice.service.agent;

import java.time.Instant;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.example.urlshortener.orchestratorservice.service.engine.WorkflowStates;

@Component
@Order(6)
public class DocumentationAgent implements WorkflowAgent {

    @Override
    public String name() {
        return "DocumentationAgent";
    }

    @Override
    public List<String> dependencies() {
        return List.of("TestingAgent");
    }

    @Override
    public String completedState() {
        return WorkflowStates.DOCUMENTATION_COMPLETED;
    }

    @Override
    public void execute(WorkflowRuntimeContext context) {
        context.put("documentation.completedAt", Instant.now().toString());
    }
}