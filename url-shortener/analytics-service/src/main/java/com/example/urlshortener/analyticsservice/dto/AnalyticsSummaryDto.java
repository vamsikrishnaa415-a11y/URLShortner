package com.example.urlshortener.analyticsservice.dto;

import java.util.List;

public record AnalyticsSummaryDto(String shortCode, long totalClicks, List<ClickAnalyticsResponseDto> events) {
}