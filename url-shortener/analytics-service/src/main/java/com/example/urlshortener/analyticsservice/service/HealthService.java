package com.example.urlshortener.analyticsservice.service;

import java.time.Instant;
import org.springframework.stereotype.Service;
import com.example.urlshortener.analyticsservice.common.ServiceMetadata;
import com.example.urlshortener.analyticsservice.dto.HealthResponse;

@Service
public class HealthService {
    public HealthResponse getHealth() {
        return new HealthResponse("UP", ServiceMetadata.SERVICE_NAME, Instant.now());
    }
}