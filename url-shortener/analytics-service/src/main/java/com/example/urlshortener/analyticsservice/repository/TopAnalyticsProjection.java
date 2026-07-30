package com.example.urlshortener.analyticsservice.repository;

public interface TopAnalyticsProjection {
    String getShortCode();

    long getTotalClicks();
}