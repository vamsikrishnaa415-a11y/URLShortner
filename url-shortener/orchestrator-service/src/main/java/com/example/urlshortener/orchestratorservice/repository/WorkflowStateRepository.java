package com.example.urlshortener.orchestratorservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.urlshortener.orchestratorservice.entity.WorkflowState;

@Repository
public interface WorkflowStateRepository extends JpaRepository<WorkflowState, Long> {

    Optional<WorkflowState> findByStateKey(String stateKey);
}