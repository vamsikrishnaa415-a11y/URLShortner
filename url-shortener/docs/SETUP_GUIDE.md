# Setup Guide

## Prerequisites

- JDK 17
- Maven 3.9+
- Git

## Clone and build

1. Clone repository.
2. Open terminal at url-shortener.
3. Run:

```bash
mvn clean verify
```

## Run services

Start each service in a separate terminal from url-shortener.

```bash
mvn -pl api-gateway spring-boot:run
mvn -pl url-service spring-boot:run
mvn -pl analytics-service spring-boot:run
mvn -pl orchestrator-service spring-boot:run
```

## Service ports

- api-gateway: 8080
- url-service: 8081
- analytics-service: 8082
- orchestrator-service: 8083

## Smoke checks

- GET http://localhost:8081/internal/health
- GET http://localhost:8082/internal/health
- GET http://localhost:8083/internal/health
- GET http://localhost:8080/internal/health

## OpenAPI checks

- http://localhost:8081/swagger-ui.html
- http://localhost:8082/swagger-ui.html
- http://localhost:8083/swagger-ui.html
- http://localhost:8080/swagger-ui.html

## Local configuration notes

- Each module uses in-memory H2 by default.
- All data resets on restart.
- url-service calls analytics-service at analytics.service.base-url (default http://localhost:8082).