package com.example.urlshortener.orchestratorservice.service.engine;

public final class WorkflowStates {
    public static final String STARTED = "STARTED";
    public static final String REQUIREMENT_COMPLETED = "REQUIREMENT_COMPLETED";
    public static final String PLANNING_COMPLETED = "PLANNING_COMPLETED";
    public static final String ARCHITECTURE_COMPLETED = "ARCHITECTURE_COMPLETED";
    public static final String IMPLEMENTATION_COMPLETED = "IMPLEMENTATION_COMPLETED";
    public static final String TESTING_COMPLETED = "TESTING_COMPLETED";
    public static final String DOCUMENTATION_COMPLETED = "DOCUMENTATION_COMPLETED";
    public static final String REVIEW_COMPLETED = "REVIEW_COMPLETED";
    public static final String APPROVAL_PENDING = "APPROVAL_PENDING";
    public static final String APPROVED = "APPROVED";
    public static final String RETRY_PENDING = "RETRY_PENDING";
    public static final String ROLLED_BACK = "ROLLED_BACK";
    public static final String SAFE_STOPPED = "SAFE_STOPPED";
    public static final String REPLANNED = "REPLANNED";

    private WorkflowStates() {
    }
}