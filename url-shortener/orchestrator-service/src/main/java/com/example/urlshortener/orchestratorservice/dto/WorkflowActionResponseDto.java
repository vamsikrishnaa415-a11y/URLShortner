package com.example.urlshortener.orchestratorservice.dto;

public record WorkflowActionResponseDto(Long workflowId, String state, String message) {
}