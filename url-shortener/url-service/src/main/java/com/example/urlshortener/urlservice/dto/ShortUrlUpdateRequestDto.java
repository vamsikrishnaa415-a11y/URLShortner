package com.example.urlshortener.urlservice.dto;

import java.time.Instant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.example.urlshortener.urlservice.validation.ValidationPatterns;

public record ShortUrlUpdateRequestDto(
        @NotBlank @Size(max = 2048) @Pattern(regexp = ValidationPatterns.URL_PATTERN) String originalUrl,
        @Size(max = 64) @Pattern(regexp = "^[A-Za-z0-9_-]*$") String customAlias,
        Instant expiryDate,
        @NotNull Boolean active) {
}