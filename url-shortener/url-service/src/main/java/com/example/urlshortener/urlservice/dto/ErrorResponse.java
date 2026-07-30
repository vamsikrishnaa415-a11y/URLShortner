package com.example.urlshortener.urlservice.dto;

import java.time.Instant;

public record ErrorResponse(
	Instant timestamp,
	int status,
	String error,
	String message,
	String path,
	String correlationId) {
}