package com.example.urlshortener.orchestratorservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.example.urlshortener.orchestratorservice.dto.ApprovalHistoryDto;
import com.example.urlshortener.orchestratorservice.dto.WorkflowActionResponseDto;
import com.example.urlshortener.orchestratorservice.dto.WorkflowAuditTrailDto;
import com.example.urlshortener.orchestratorservice.dto.WorkflowContextEntryDto;
import com.example.urlshortener.orchestratorservice.dto.WorkflowDetailResponseDto;
import com.example.urlshortener.orchestratorservice.service.WorkflowEngineService;

@WebMvcTest(controllers = WorkflowController.class)
class WorkflowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkflowEngineService workflowEngineService;

    @Test
    void shouldStartWorkflow() throws Exception {
        when(workflowEngineService.startWorkflow(any()))
                .thenReturn(new WorkflowActionResponseDto(11L, "APPROVAL_PENDING", "started"));

        mockMvc.perform(post("/workflow/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"workflowName\":\"ReleaseFlow\",\"initiatedBy\":\"lead\",\"correlationId\":\"corr-1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.workflowId").value(11));
    }

    @Test
    void shouldGetWorkflowById() throws Exception {
        WorkflowDetailResponseDto response = new WorkflowDetailResponseDto(
                11L,
                "ReleaseFlow",
                "corr-1",
                Instant.now(),
                null,
                "lead",
                "APPROVAL_PENDING",
                Map.of("RequirementAgent", List.of()),
                List.of(new WorkflowContextEntryDto("ticket", "URL-123", Instant.now())),
                List.of(new ApprovalHistoryDto(1L, 11L, "qa", "APPROVE", "ok", Instant.now())),
                List.of(new WorkflowAuditTrailDto("WORKFLOW_STARTED", "started", Instant.now())));

        when(workflowEngineService.getWorkflow(11L)).thenReturn(response);

        mockMvc.perform(get("/workflow/11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(11))
                .andExpect(jsonPath("$.currentState").value("APPROVAL_PENDING"));
    }

    @Test
    void shouldApproveWorkflow() throws Exception {
        when(workflowEngineService.approveWorkflow(eq(11L), any()))
                .thenReturn(new WorkflowActionResponseDto(11L, "APPROVED", "approved"));

        mockMvc.perform(post("/workflow/11/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"approver\":\"qa\",\"decision\":\"APPROVE\",\"comments\":\"ok\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("APPROVED"));
    }

    @Test
    void shouldRetryWorkflow() throws Exception {
        when(workflowEngineService.retryWorkflow(eq(11L), any()))
                .thenReturn(new WorkflowActionResponseDto(11L, "APPROVAL_PENDING", "retried"));

        mockMvc.perform(post("/workflow/11/retry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"rerun\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("APPROVAL_PENDING"));
    }

    @Test
    void shouldRollbackWorkflow() throws Exception {
        when(workflowEngineService.rollbackWorkflow(eq(11L), any()))
                .thenReturn(new WorkflowActionResponseDto(11L, "ROLLED_BACK", "rolled back"));

        mockMvc.perform(post("/workflow/11/rollback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"manual rollback\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("ROLLED_BACK"));
    }
}