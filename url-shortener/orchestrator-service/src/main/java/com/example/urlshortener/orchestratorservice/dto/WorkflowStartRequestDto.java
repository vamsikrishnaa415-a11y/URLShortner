package com.example.urlshortener.orchestratorservice.dto;

import java.util.Map;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WorkflowStartRequestDto(
        @NotBlank @Size(max = 128) String workflowName,
        @NotBlank @Size(max = 64) String initiatedBy,
        @Size(max = 64) String correlationId,
        Map<String, String> initialContext) {
}