package com.example.urlshortener.orchestratorservice.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.urlshortener.orchestratorservice.dto.WorkflowActionRequestDto;
import com.example.urlshortener.orchestratorservice.dto.WorkflowActionResponseDto;
import com.example.urlshortener.orchestratorservice.dto.WorkflowApprovalRequestDto;
import com.example.urlshortener.orchestratorservice.dto.WorkflowDetailResponseDto;
import com.example.urlshortener.orchestratorservice.dto.WorkflowStartRequestDto;
import com.example.urlshortener.orchestratorservice.entity.ApprovalHistory;
import com.example.urlshortener.orchestratorservice.entity.WorkflowAuditTrail;
import com.example.urlshortener.orchestratorservice.entity.WorkflowContextEntry;
import com.example.urlshortener.orchestratorservice.entity.WorkflowExecution;
import com.example.urlshortener.orchestratorservice.entity.WorkflowState;
import com.example.urlshortener.orchestratorservice.exception.BadRequestException;
import com.example.urlshortener.orchestratorservice.exception.ResourceNotFoundException;
import com.example.urlshortener.orchestratorservice.exception.ServiceException;
import com.example.urlshortener.orchestratorservice.mapper.WorkflowOrchestrationMapper;
import com.example.urlshortener.orchestratorservice.repository.ApprovalHistoryRepository;
import com.example.urlshortener.orchestratorservice.repository.WorkflowAuditTrailRepository;
import com.example.urlshortener.orchestratorservice.repository.WorkflowContextEntryRepository;
import com.example.urlshortener.orchestratorservice.repository.WorkflowExecutionRepository;
import com.example.urlshortener.orchestratorservice.repository.WorkflowStateRepository;
import com.example.urlshortener.orchestratorservice.service.agent.WorkflowAgent;
import com.example.urlshortener.orchestratorservice.service.agent.WorkflowRuntimeContext;
import com.example.urlshortener.orchestratorservice.service.engine.WorkflowDependencyGraph;
import com.example.urlshortener.orchestratorservice.service.engine.WorkflowStateMachine;
import com.example.urlshortener.orchestratorservice.service.engine.WorkflowStates;

@Service
public class WorkflowEngineService {

    private final WorkflowExecutionRepository workflowExecutionRepository;
    private final WorkflowStateRepository workflowStateRepository;
    private final ApprovalHistoryRepository approvalHistoryRepository;
    private final WorkflowContextEntryRepository workflowContextEntryRepository;
    private final WorkflowAuditTrailRepository workflowAuditTrailRepository;
    private final WorkflowOrchestrationMapper workflowOrchestrationMapper;
    private final WorkflowStateMachine workflowStateMachine;
    private final WorkflowDependencyGraph workflowDependencyGraph;
    private final List<WorkflowAgent> workflowAgents;

    public WorkflowEngineService(
            WorkflowExecutionRepository workflowExecutionRepository,
            WorkflowStateRepository workflowStateRepository,
            ApprovalHistoryRepository approvalHistoryRepository,
            WorkflowContextEntryRepository workflowContextEntryRepository,
            WorkflowAuditTrailRepository workflowAuditTrailRepository,
            WorkflowOrchestrationMapper workflowOrchestrationMapper,
            WorkflowStateMachine workflowStateMachine,
            WorkflowDependencyGraph workflowDependencyGraph,
            List<WorkflowAgent> workflowAgents) {
        this.workflowExecutionRepository = workflowExecutionRepository;
        this.workflowStateRepository = workflowStateRepository;
        this.approvalHistoryRepository = approvalHistoryRepository;
        this.workflowContextEntryRepository = workflowContextEntryRepository;
        this.workflowAuditTrailRepository = workflowAuditTrailRepository;
        this.workflowOrchestrationMapper = workflowOrchestrationMapper;
        this.workflowStateMachine = workflowStateMachine;
        this.workflowDependencyGraph = workflowDependencyGraph;
        this.workflowAgents = workflowAgents;
    }

    @Transactional
    public WorkflowActionResponseDto startWorkflow(WorkflowStartRequestDto request) {
        String correlationId = request.correlationId() == null || request.correlationId().isBlank()
                ? UUID.randomUUID().toString()
                : request.correlationId();

        WorkflowExecution execution = new WorkflowExecution();
        execution.setWorkflowName(request.workflowName());
        execution.setInitiatedBy(request.initiatedBy());
        execution.setCorrelationId(correlationId);
        execution.setStartedAt(Instant.now());
        execution.setWorkflowState(resolveState(WorkflowStates.STARTED));

        WorkflowExecution saved = workflowExecutionRepository.save(execution);
        saveContext(saved, request.initialContext());
        writeAudit(saved, "WORKFLOW_STARTED", "Workflow started by " + request.initiatedBy());

        return runAgents(saved, 0, "Workflow started and executed to approval gate");
    }

    @Transactional(readOnly = true)
    public WorkflowDetailResponseDto getWorkflow(Long workflowId) {
        WorkflowExecution execution = getExecution(workflowId);
        List<WorkflowContextEntry> contextEntries = workflowContextEntryRepository.findByWorkflowExecutionId(workflowId);
        List<ApprovalHistory> decisions = approvalHistoryRepository.findByWorkflowExecutionIdOrderByDecidedAtAsc(workflowId);
        List<WorkflowAuditTrail> auditTrail = workflowAuditTrailRepository.findByWorkflowExecutionIdOrderByCreatedAtAsc(workflowId);
        return workflowOrchestrationMapper.toDetailDto(execution, workflowDependencyGraph.asMap(), contextEntries, decisions, auditTrail);
    }

    @Transactional
    public WorkflowActionResponseDto approveWorkflow(Long workflowId, WorkflowApprovalRequestDto request) {
        WorkflowExecution execution = getExecution(workflowId);
        requireState(execution, WorkflowStates.APPROVAL_PENDING);

        ApprovalHistory history = new ApprovalHistory();
        history.setWorkflowExecution(execution);
        history.setApprover(request.approver());
        history.setDecision(request.decision());
        history.setComments(request.comments());
        history.setDecidedAt(Instant.now());
        approvalHistoryRepository.save(history);

        if ("APPROVE".equalsIgnoreCase(request.decision())) {
            transitionState(execution, WorkflowStates.APPROVED);
            execution.setCompletedAt(Instant.now());
            workflowExecutionRepository.save(execution);
            writeAudit(execution, "APPROVED", "Workflow approved by " + request.approver());
            return new WorkflowActionResponseDto(execution.getId(), execution.getWorkflowState().getStateKey(), "Workflow approved");
        }

        transitionState(execution, WorkflowStates.REPLANNED);
        workflowExecutionRepository.save(execution);
        upsertContext(execution, "replan.reason", request.comments() == null ? "Rejected" : request.comments());
        writeAudit(execution, "REPLANNED", "Workflow rejected and marked for dynamic replanning");
        return new WorkflowActionResponseDto(execution.getId(), execution.getWorkflowState().getStateKey(), "Workflow rejected and replanned");
    }

    @Transactional
    public WorkflowActionResponseDto retryWorkflow(Long workflowId, WorkflowActionRequestDto request) {
        WorkflowExecution execution = getExecution(workflowId);
        String current = execution.getWorkflowState().getStateKey();
        if (!WorkflowStates.SAFE_STOPPED.equals(current) && !WorkflowStates.REPLANNED.equals(current)) {
            throw new BadRequestException("Retry is allowed only from SAFE_STOPPED or REPLANNED state");
        }

        transitionState(execution, WorkflowStates.RETRY_PENDING);
        workflowExecutionRepository.save(execution);
        upsertContext(execution, "retry.reason", request == null ? null : request.reason());
        writeAudit(execution, "RETRY_REQUESTED", request == null ? "Retry requested" : request.reason());

        int startIndex = WorkflowStates.REPLANNED.equals(current) ? 1 : 0;
        return runAgents(execution, startIndex, "Retry execution completed to approval gate");
    }

    @Transactional
    public WorkflowActionResponseDto rollbackWorkflow(Long workflowId, WorkflowActionRequestDto request) {
        WorkflowExecution execution = getExecution(workflowId);
        transitionState(execution, WorkflowStates.ROLLED_BACK);
        execution.setCompletedAt(Instant.now());
        workflowExecutionRepository.save(execution);

        upsertContext(execution, "rollback.reason", request == null ? null : request.reason());
        writeAudit(execution, "ROLLED_BACK", request == null ? "Rollback requested" : request.reason());
        return new WorkflowActionResponseDto(execution.getId(), execution.getWorkflowState().getStateKey(), "Workflow rolled back");
    }

    private WorkflowActionResponseDto runAgents(WorkflowExecution execution, int startIndex, String successMessage) {
        Map<String, String> context = loadContextMap(execution.getId());
        WorkflowRuntimeContext runtimeContext = new WorkflowRuntimeContext(execution.getId(), context);

        try {
            for (int i = startIndex; i < workflowAgents.size(); i++) {
                WorkflowAgent agent = workflowAgents.get(i);
                assertDependencies(runtimeContext, agent);

                agent.execute(runtimeContext);
                runtimeContext.put("agent." + agent.name() + ".completed", "true");
                transitionState(execution, agent.completedState());
                workflowExecutionRepository.save(execution);
                writeAudit(execution, "AGENT_COMPLETED", agent.name() + " completed");
            }

            saveContext(execution, runtimeContext.getContextEntries());
            return new WorkflowActionResponseDto(execution.getId(), execution.getWorkflowState().getStateKey(), successMessage);
        } catch (Exception ex) {
            transitionState(execution, WorkflowStates.SAFE_STOPPED);
            workflowExecutionRepository.save(execution);
            writeAudit(execution, "SAFE_STOP", "Safe stop triggered: " + ex.getMessage());
            saveContext(execution, runtimeContext.getContextEntries());
            throw new ServiceException("Workflow entered safe-stop state: " + ex.getMessage());
        }
    }

    private void assertDependencies(WorkflowRuntimeContext context, WorkflowAgent agent) {
        for (String dependency : agent.dependencies()) {
            String key = "agent." + dependency + ".completed";
            if (!"true".equals(context.get(key))) {
                throw new BadRequestException("Dependency not satisfied for " + agent.name() + ": " + dependency);
            }
        }
    }

    private WorkflowExecution getExecution(Long workflowId) {
        return workflowExecutionRepository.findById(workflowId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow not found: " + workflowId));
    }

    private WorkflowState resolveState(String stateKey) {
        return workflowStateRepository.findByStateKey(stateKey)
                .orElseGet(() -> {
                    WorkflowState state = new WorkflowState();
                    state.setStateKey(stateKey);
                    state.setDescription(stateKey.replace('_', ' '));
                    state.setTerminal(WorkflowStates.APPROVED.equals(stateKey) || WorkflowStates.ROLLED_BACK.equals(stateKey));
                    return workflowStateRepository.save(state);
                });
    }

    private void transitionState(WorkflowExecution execution, String targetState) {
        String from = execution.getWorkflowState() == null ? null : execution.getWorkflowState().getStateKey();
        if (!workflowStateMachine.canTransition(from, targetState)) {
            throw new BadRequestException("Invalid state transition from " + from + " to " + targetState);
        }
        execution.setWorkflowState(resolveState(targetState));
    }

    private Map<String, String> loadContextMap(Long workflowId) {
        Map<String, String> map = new HashMap<>();
        for (WorkflowContextEntry entry : workflowContextEntryRepository.findByWorkflowExecutionId(workflowId)) {
            map.put(entry.getContextKey(), entry.getContextValue());
        }
        return map;
    }

    private void saveContext(WorkflowExecution execution, Map<String, String> contextMap) {
        if (contextMap == null) {
            return;
        }
        for (Map.Entry<String, String> item : contextMap.entrySet()) {
            upsertContext(execution, item.getKey(), item.getValue());
        }
    }

    private void upsertContext(WorkflowExecution execution, String key, String value) {
        if (key == null || key.isBlank()) {
            return;
        }
        WorkflowContextEntry entry = workflowContextEntryRepository
                .findByWorkflowExecutionIdAndContextKey(execution.getId(), key)
                .orElseGet(() -> {
                    WorkflowContextEntry fresh = new WorkflowContextEntry();
                    fresh.setWorkflowExecution(execution);
                    fresh.setContextKey(key);
                    return fresh;
                });
        entry.setContextValue(value);
        entry.setUpdatedAt(Instant.now());
        workflowContextEntryRepository.save(entry);
    }

    private void requireState(WorkflowExecution execution, String expected) {
        String current = execution.getWorkflowState().getStateKey();
        if (!expected.equals(current)) {
            throw new BadRequestException("Expected state " + expected + " but found " + current);
        }
    }

    private void writeAudit(WorkflowExecution execution, String action, String details) {
        WorkflowAuditTrail trail = new WorkflowAuditTrail();
        trail.setWorkflowExecution(execution);
        trail.setAction(action);
        trail.setDetails(details);
        trail.setCreatedAt(Instant.now());
        workflowAuditTrailRepository.save(trail);
    }
}