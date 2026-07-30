package com.example.urlshortener.orchestratorservice.service.engine;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.example.urlshortener.orchestratorservice.service.agent.WorkflowAgent;

@Component
public class WorkflowDependencyGraph {

    private final List<WorkflowAgent> agents;

    public WorkflowDependencyGraph(List<WorkflowAgent> agents) {
        this.agents = agents;
    }

    public Map<String, List<String>> asMap() {
        Map<String, List<String>> graph = new LinkedHashMap<>();
        for (WorkflowAgent agent : agents) {
            graph.put(agent.name(), agent.dependencies());
        }
        return graph;
    }
}