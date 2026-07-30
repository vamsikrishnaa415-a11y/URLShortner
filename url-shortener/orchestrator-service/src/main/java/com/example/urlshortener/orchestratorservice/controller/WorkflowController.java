package com.example.urlshortener.orchestratorservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.urlshortener.orchestratorservice.dto.ErrorResponse;
import com.example.urlshortener.orchestratorservice.dto.WorkflowActionRequestDto;
import com.example.urlshortener.orchestratorservice.dto.WorkflowActionResponseDto;
import com.example.urlshortener.orchestratorservice.dto.WorkflowApprovalRequestDto;
import com.example.urlshortener.orchestratorservice.dto.WorkflowDetailResponseDto;
import com.example.urlshortener.orchestratorservice.dto.WorkflowStartRequestDto;
import com.example.urlshortener.orchestratorservice.service.WorkflowEngineService;

@Validated
@RestController
@RequestMapping("/workflow")
@Tag(name = "Agentic SDLC Orchestrator", description = "Workflow orchestration APIs")
public class WorkflowController {

    private final WorkflowEngineService workflowEngineService;

    public WorkflowController(WorkflowEngineService workflowEngineService) {
        this.workflowEngineService = workflowEngineService;
    }

    @Operation(summary = "Start a workflow")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Workflow started"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/start")
    public ResponseEntity<WorkflowActionResponseDto> startWorkflow(@Valid @RequestBody WorkflowStartRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowEngineService.startWorkflow(request));
    }

    @Operation(summary = "Get workflow details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Workflow found"),
            @ApiResponse(responseCode = "404", description = "Workflow not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<WorkflowDetailResponseDto> getWorkflow(@PathVariable("id") @Positive Long workflowId) {
        return ResponseEntity.ok(workflowEngineService.getWorkflow(workflowId));
    }

    @Operation(summary = "Approve or reject a workflow at approval gate")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Workflow approval processed"),
            @ApiResponse(responseCode = "400", description = "Invalid state or decision", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Workflow not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/approve")
    public ResponseEntity<WorkflowActionResponseDto> approveWorkflow(
            @PathVariable("id") @Positive Long workflowId,
            @Valid @RequestBody WorkflowApprovalRequestDto request) {
        return ResponseEntity.ok(workflowEngineService.approveWorkflow(workflowId, request));
    }

    @Operation(summary = "Retry workflow execution after safe-stop or replanning")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retry executed"),
            @ApiResponse(responseCode = "400", description = "Invalid retry state", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Workflow not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/retry")
    public ResponseEntity<WorkflowActionResponseDto> retryWorkflow(
            @PathVariable("id") @Positive Long workflowId,
            @RequestBody(required = false) WorkflowActionRequestDto request) {
        return ResponseEntity.ok(workflowEngineService.retryWorkflow(workflowId, request));
    }

    @Operation(summary = "Rollback workflow")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rollback executed"),
            @ApiResponse(responseCode = "404", description = "Workflow not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/rollback")
    public ResponseEntity<WorkflowActionResponseDto> rollbackWorkflow(
            @PathVariable("id") @Positive Long workflowId,
            @RequestBody(required = false) WorkflowActionRequestDto request) {
        return ResponseEntity.ok(workflowEngineService.rollbackWorkflow(workflowId, request));
    }
}