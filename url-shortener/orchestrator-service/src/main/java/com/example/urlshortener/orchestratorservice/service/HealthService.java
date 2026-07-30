package com.example.urlshortener.orchestratorservice.service;

import java.time.Instant;
import org.springframework.stereotype.Service;
import com.example.urlshortener.orchestratorservice.common.ServiceMetadata;
import com.example.urlshortener.orchestratorservice.dto.HealthResponse;

@Service
public class HealthService {
    public HealthResponse getHealth() {
        return new HealthResponse("UP", ServiceMetadata.SERVICE_NAME, Instant.now());
    }
}