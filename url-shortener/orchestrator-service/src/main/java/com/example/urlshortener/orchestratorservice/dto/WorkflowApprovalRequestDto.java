package com.example.urlshortener.orchestratorservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record WorkflowApprovalRequestDto(
        @NotBlank @Size(max = 64) String approver,
        @NotBlank @Pattern(regexp = "APPROVE|REJECT") String decision,
        @Size(max = 512) String comments) {
}