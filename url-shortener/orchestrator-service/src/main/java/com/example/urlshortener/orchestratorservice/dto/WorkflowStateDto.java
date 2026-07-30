package com.example.urlshortener.orchestratorservice.dto;

public record WorkflowStateDto(Long id, String stateKey, String description, Boolean terminal) {
}