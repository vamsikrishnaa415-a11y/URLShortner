package com.example.urlshortener.analyticsservice.util;

import java.util.UUID;

public final class CorrelationIdUtil {
    private CorrelationIdUtil() {
    }

    public static String getOrCreate() {
        return UUID.randomUUID().toString();
    }
}