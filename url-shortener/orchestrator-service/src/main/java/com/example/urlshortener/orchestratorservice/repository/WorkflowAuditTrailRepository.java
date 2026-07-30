package com.example.urlshortener.orchestratorservice.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.urlshortener.orchestratorservice.entity.WorkflowAuditTrail;

@Repository
public interface WorkflowAuditTrailRepository extends JpaRepository<WorkflowAuditTrail, Long> {

    List<WorkflowAuditTrail> findByWorkflowExecutionIdOrderByCreatedAtAsc(Long workflowExecutionId);
}