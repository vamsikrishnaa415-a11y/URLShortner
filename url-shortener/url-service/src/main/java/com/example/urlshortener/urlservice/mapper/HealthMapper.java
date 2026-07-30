package com.example.urlshortener.urlservice.mapper;

import com.example.urlshortener.urlservice.dto.HealthResponse;
import com.example.urlshortener.urlservice.entity.HealthEntity;

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