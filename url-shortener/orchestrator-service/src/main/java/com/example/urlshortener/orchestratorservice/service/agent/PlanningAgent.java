package com.example.urlshortener.orchestratorservice.service.agent;

import java.time.Instant;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.example.urlshortener.orchestratorservice.service.engine.WorkflowStates;

@Component
@Order(2)
public class PlanningAgent implements WorkflowAgent {

    @Override
    public String name() {
        return "PlanningAgent";
    }

    @Override
    public List<String> dependencies() {
        return List.of("RequirementAgent");
    }

    @Override
    public String completedState() {
        return WorkflowStates.PLANNING_COMPLETED;
    }

    @Override
    public void execute(WorkflowRuntimeContext context) {
        context.put("planning.completedAt", Instant.now().toString());
    }
}
