package com.example.urlshortener.urlservice.service;

import java.time.Instant;
import org.springframework.stereotype.Service;
import com.example.urlshortener.urlservice.common.ServiceMetadata;
import com.example.urlshortener.urlservice.dto.HealthResponse;

@Service
public class HealthService {
    public HealthResponse getHealth() {
        return new HealthResponse("UP", ServiceMetadata.SERVICE_NAME, Instant.now());
    }
}