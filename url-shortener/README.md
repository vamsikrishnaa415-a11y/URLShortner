# URL Shortener microservices

This module contains four Spring Boot services that work together as a small microservice landscape.

## Services

- api-gateway: receives external traffic and routes requests to internal services.
- url-service: stores and resolves short URLs.
- analytics-service: tracks access metrics and usage patterns.
- orchestrator-service: coordinates workflows across the other services.

## Structure

- api-gateway/
- url-service/
- analytics-service/
- orchestrator-service/

Each service contains a standard Spring Boot layout with config, controller, service, repository, entity, dto, mapper, exception, validation, util, and common packages.

## Run each module

From the project root run:

- mvn -pl api-gateway spring-boot:run
- mvn -pl url-service spring-boot:run
- mvn -pl analytics-service spring-boot:run
- mvn -pl orchestrator-service spring-boot:run

## Run the full project

From url-shortener/ run:

- mvn spring-boot:run -pl api-gateway

Each service exposes a health endpoint at /internal/health and Swagger UI at /swagger-ui.html.

## Database Model (Commit 7)

H2-backed JPA data model has been added for core microservices.

- url-service
	- Entity: ShortUrl (id, originalUrl, shortCode, customAlias, createdAt, expiryDate, active, clickCount)
	- Repository: ShortUrlRepository
	- DTOs: ShortUrlRequestDto, ShortUrlResponseDto

- analytics-service
	- Entity: ClickAnalytics (id, shortCode, clickedAt, ipAddress, browser, device, operatingSystem, referrer)
	- Repository: ClickAnalyticsRepository
	- DTOs: ClickAnalyticsRequestDto, ClickAnalyticsResponseDto

- orchestrator-service
	- Entities: WorkflowExecution, WorkflowState, ApprovalHistory
	- Repositories: WorkflowExecutionRepository, WorkflowStateRepository, ApprovalHistoryRepository
	- DTOs: WorkflowExecutionDto, WorkflowStateDto, ApprovalHistoryDto

Repository unit tests are included for these new repositories.

## URL Service API (Current Commit)

Implemented endpoints in url-service:

- POST /api/v1/urls
- GET /api/v1/urls/{shortCode}
- PUT /api/v1/urls/{id}
- DELETE /api/v1/urls/{id}
- GET /r/{shortCode}

Behavior delivered:

- Random short code generation when alias is not provided.
- Custom alias support with duplicate alias validation.
- URL format validation through Jakarta validation constraints.
- Expiration date validation and expiration enforcement during redirect.
- Enable or disable URL via update endpoint.
- Structured exception handling with HTTP-specific status mapping.
- Swagger/OpenAPI annotations for URL endpoints.
- Unit tests for service and controller layers.