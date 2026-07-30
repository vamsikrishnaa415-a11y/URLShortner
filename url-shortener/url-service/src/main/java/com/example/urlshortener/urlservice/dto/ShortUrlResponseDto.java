package com.example.urlshortener.urlservice.dto;

import java.time.Instant;

public record ShortUrlResponseDto(
        Long id,
        String originalUrl,
        String shortCode,
        String customAlias,
        Instant createdAt,
        Instant expiryDate,
        Boolean active,
        Long clickCount) {
}