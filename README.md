# URL Shortener Platform

Multi-module Spring Boot platform for URL shortening, redirect analytics, and agentic SDLC orchestration.

## Quick links

- Project implementation: [url-shortener/README.md](url-shortener/README.md)
- Final engineering docs index: [url-shortener/docs/README.md](url-shortener/docs/README.md)

## Stack snapshot

- Java 17
- Spring Boot 3.3.2
- Spring Cloud OpenFeign + Resilience4j (url-service integration)
- Spring Data JPA
- H2 in-memory databases per service

## Services

- api-gateway
- url-service
- analytics-service
- orchestrator-service

## Current status

- Core APIs implemented for URL management, redirect analytics, and workflow orchestration.
- Unit and slice tests are green across all modules.
- Production readiness review and final architecture documentation are available in [url-shortener/docs/README.md](url-shortener/docs/README.md).
