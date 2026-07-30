package com.example.urlshortener.orchestratorservice.service.agent;

import java.util.HashMap;
import java.util.Map;

public class WorkflowRuntimeContext {

    private final Long workflowId;
    private final Map<String, String> contextEntries = new HashMap<>();

    public WorkflowRuntimeContext(Long workflowId, Map<String, String> seedData) {
        this.workflowId = workflowId;
        if (seedData != null) {
            this.contextEntries.putAll(seedData);
        }
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public Map<String, String> getContextEntries() {
        return contextEntries;
    }

    public void put(String key, String value) {
        contextEntries.put(key, value);
    }

    public String get(String key) {
        return contextEntries.get(key);
    }
}