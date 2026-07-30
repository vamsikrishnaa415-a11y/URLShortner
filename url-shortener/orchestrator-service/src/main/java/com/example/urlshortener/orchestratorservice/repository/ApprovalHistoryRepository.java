package com.example.urlshortener.orchestratorservice.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.urlshortener.orchestratorservice.entity.ApprovalHistory;

@Repository
public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {

    List<ApprovalHistory> findByWorkflowExecutionIdOrderByDecidedAtAsc(Long workflowExecutionId);
}