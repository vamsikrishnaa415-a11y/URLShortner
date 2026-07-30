package com.example.urlshortener.orchestratorservice.util;

import java.util.UUID;

public final class CorrelationIdUtil {
    private CorrelationIdUtil() {
    }

    public static String getOrCreate() {
        return UUID.randomUUID().toString();
    }
}