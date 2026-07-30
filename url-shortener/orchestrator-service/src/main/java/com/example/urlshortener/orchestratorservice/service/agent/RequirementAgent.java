package com.example.urlshortener.orchestratorservice.service.agent;

import java.time.Instant;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.example.urlshortener.orchestratorservice.service.engine.WorkflowStates;

@Component
@Order(1)
public class RequirementAgent implements WorkflowAgent {

    @Override
    public String name() {
        return "RequirementAgent";
    }

    @Override
    public List<String> dependencies() {
        return List.of();
    }

    @Override
    public String completedState() {
        return WorkflowStates.REQUIREMENT_COMPLETED;
    }

    @Override
    public void execute(WorkflowRuntimeContext context) {
        context.put("requirement.completedAt", Instant.now().toString());
    }
}