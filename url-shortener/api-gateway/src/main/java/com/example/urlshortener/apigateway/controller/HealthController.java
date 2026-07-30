package com.example.urlshortener.apigateway.controller;

import java.util.Objects;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.urlshortener.apigateway.dto.HealthResponse;
import com.example.urlshortener.apigateway.service.HealthService;

@RestController
@RequestMapping("/internal")
public class HealthController {
    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = Objects.requireNonNull(healthService, "healthService");
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(healthService.getHealth());
    }
}