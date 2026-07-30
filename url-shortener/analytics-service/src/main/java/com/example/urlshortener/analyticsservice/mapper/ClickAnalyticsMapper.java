package com.example.urlshortener.analyticsservice.mapper;

import org.springframework.stereotype.Component;
import com.example.urlshortener.analyticsservice.dto.ClickAnalyticsRequestDto;
import com.example.urlshortener.analyticsservice.dto.ClickAnalyticsResponseDto;
import com.example.urlshortener.analyticsservice.entity.ClickAnalytics;

@Component
public class ClickAnalyticsMapper {

    public ClickAnalytics toEntity(ClickAnalyticsRequestDto request) {
        ClickAnalytics entity = new ClickAnalytics();
        entity.setShortCode(request.shortCode());
        entity.setClickedAt(request.clickedAt());
        entity.setIpAddress(request.ipAddress());
        entity.setBrowser(request.browser());
        entity.setDevice(request.device());
        entity.setOperatingSystem(request.operatingSystem());
        entity.setReferrer(request.referrer());
        return entity;
    }

    public ClickAnalyticsResponseDto toResponseDto(ClickAnalytics entity) {
        return new ClickAnalyticsResponseDto(
                entity.getId(),
                entity.getShortCode(),
                entity.getClickedAt(),
                entity.getIpAddress(),
                entity.getBrowser(),
                entity.getDevice(),
                entity.getOperatingSystem(),
                entity.getReferrer());
    }
}