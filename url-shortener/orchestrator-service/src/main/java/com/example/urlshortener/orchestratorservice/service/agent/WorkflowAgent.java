package com.example.urlshortener.orchestratorservice.service.agent;

import java.util.List;

public interface WorkflowAgent {

    String name();

    List<String> dependencies();

    String completedState();

    void execute(WorkflowRuntimeContext context);
}