package com.example.urlshortener.orchestratorservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.urlshortener.orchestratorservice.entity.WorkflowExecution;

@Repository
public interface WorkflowExecutionRepository extends JpaRepository<WorkflowExecution, Long> {

    Optional<WorkflowExecution> findByCorrelationId(String correlationId);
}