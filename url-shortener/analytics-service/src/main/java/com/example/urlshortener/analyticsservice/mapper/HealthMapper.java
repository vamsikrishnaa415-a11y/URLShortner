package com.example.urlshortener.analyticsservice.mapper;

import com.example.urlshortener.analyticsservice.dto.HealthResponse;
import com.example.urlshortener.analyticsservice.entity.HealthEntity;

public final class HealthMapper {
    private HealthMapper() {
    }

    public static HealthResponse toHealthResponse(HealthEntity entity) {
        if (entity == null) {
            return null;
        }
        return new HealthResponse(entity.getStatus() == null ? "UNKNOWN" : entity.getStatus(), null, entity.getCheckedAt());
    }
}