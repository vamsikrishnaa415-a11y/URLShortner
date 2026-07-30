package com.example.urlshortener.orchestratorservice.service.agent;

import java.time.Instant;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.example.urlshortener.orchestratorservice.service.engine.WorkflowStates;

@Component
@Order(8)
public class ApprovalAgent implements WorkflowAgent {

    @Override
    public String name() {
        return "ApprovalAgent";
    }

    @Override
    public List<String> dependencies() {
        return List.of("ReviewerAgent");
    }

    @Override
    public String completedState() {
        return WorkflowStates.APPROVAL_PENDING;
    }

    @Override
    public void execute(WorkflowRuntimeContext context) {
        context.put("approval.requiredAt", Instant.now().toString());
    }
}