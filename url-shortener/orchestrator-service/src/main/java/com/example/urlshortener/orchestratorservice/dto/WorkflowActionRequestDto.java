package com.example.urlshortener.orchestratorservice.dto;

import jakarta.validation.constraints.Size;

public record WorkflowActionRequestDto(@Size(max = 512) String reason) {
}