package com.example.urlshortener.orchestratorservice.service.agent;

import java.time.Instant;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.example.urlshortener.orchestratorservice.service.engine.WorkflowStates;

@Component
@Order(7)
public class ReviewerAgent implements WorkflowAgent {

    @Override
    public String name() {
        return "ReviewerAgent";
    }

    @Override
    public List<String> dependencies() {
        return List.of("DocumentationAgent");
    }

    @Override
    public String completedState() {
        return WorkflowStates.REVIEW_COMPLETED;
    }

    @Override
    public void execute(WorkflowRuntimeContext context) {
        context.put("review.completedAt", Instant.now().toString());
    }
}