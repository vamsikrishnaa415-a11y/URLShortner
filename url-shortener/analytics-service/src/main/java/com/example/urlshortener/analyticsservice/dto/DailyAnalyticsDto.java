package com.example.urlshortener.analyticsservice.dto;

import java.time.LocalDate;

public record DailyAnalyticsDto(LocalDate date, long totalClicks) {
}