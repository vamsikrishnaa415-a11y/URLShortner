# URL Shortener Microservices

This folder contains a multi-module Spring Boot platform with four services.

## Modules

- api-gateway (port 8080): ingress and shared operational endpoints.
- url-service (port 8081): URL create/read/update/delete and redirect.
- analytics-service (port 8082): redirect event storage and analytics queries.
- orchestrator-service (port 8083): agentic workflow orchestration.

## Tech stack

- Java 17
- Spring Boot 3.3.2
- Spring Data JPA + H2
- Springdoc OpenAPI
- OpenFeign + Resilience4j (url-service to analytics-service)

## API highlights

- URL Service
	- POST /api/v1/urls
	- GET /api/v1/urls/{shortCode}
	- PUT /api/v1/urls/{id}
	- DELETE /api/v1/urls/{id}
	- GET /r/{shortCode}
- Analytics Service
	- POST /analytics/events
	- GET /analytics/{shortCode}
	- GET /analytics/top
	- GET /analytics/daily
- Orchestrator Service
	- POST /workflow/start
	- GET /workflow/{id}
	- POST /workflow/{id}/approve
	- POST /workflow/{id}/retry
	- POST /workflow/{id}/rollback

## Build and run

From this folder:

- Build all modules
	- mvn clean verify
- Run one module
	- mvn -pl url-service spring-boot:run
	- mvn -pl analytics-service spring-boot:run
	- mvn -pl orchestrator-service spring-boot:run
	- mvn -pl api-gateway spring-boot:run

## Health and OpenAPI

- Health: /internal/health
- OpenAPI JSON: /v3/api-docs
- Swagger UI: /swagger-ui.html

## Engineering deliverables

Final production-readiness artifacts are in [docs/README.md](docs/README.md):

- API documentation
- architecture diagrams
- setup and testing guides
- trade-off and risk analysis
- security review
- limitations
- final engineering summary