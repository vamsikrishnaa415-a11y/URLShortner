package com.example.urlshortener.orchestratorservice.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.urlshortener.orchestratorservice.entity.WorkflowContextEntry;

@Repository
public interface WorkflowContextEntryRepository extends JpaRepository<WorkflowContextEntry, Long> {

    List<WorkflowContextEntry> findByWorkflowExecutionId(Long workflowExecutionId);

    Optional<WorkflowContextEntry> findByWorkflowExecutionIdAndContextKey(Long workflowExecutionId, String contextKey);
}