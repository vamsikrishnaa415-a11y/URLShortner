package com.example.urlshortener.apigateway.service;

import java.time.Instant;
import org.springframework.stereotype.Service;
import com.example.urlshortener.apigateway.common.ServiceMetadata;
import com.example.urlshortener.apigateway.dto.HealthResponse;

@Service
public class HealthService {
    public HealthResponse getHealth() {
        return new HealthResponse("UP", ServiceMetadata.SERVICE_NAME, Instant.now());
    }
}