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