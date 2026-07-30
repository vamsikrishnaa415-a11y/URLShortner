# Architecture

## System overview

The platform is implemented as a Maven multi-module project with four Spring Boot services.

- api-gateway: external entrypoint and shared operational endpoints.
- url-service: core URL lifecycle and redirect handling.
- analytics-service: clickstream event storage and analytics queries.
- orchestrator-service: agentic SDLC workflow orchestration engine.

## Component architecture

```mermaid
flowchart LR
    C[Client] --> G[api-gateway :8080]
    C --> U[url-service :8081]
    C --> A[analytics-service :8082]
    C --> O[orchestrator-service :8083]

    U -->|OpenFeign + timeout + fallback| A

    U --> UH[(H2 URL DB)]
    A --> AH[(H2 Analytics DB)]
    O --> OH[(H2 Orchestrator DB)]
    G --> GH[(H2 Gateway DB)]
```

## Redirect and analytics sequence

```mermaid
sequenceDiagram
    participant Client
    participant URL as url-service
    participant Analytics as analytics-service

    Client->>URL: GET /r/{shortCode}
    URL->>URL: validate, resolve, increment click count
    URL-->>Client: 302 Location: originalUrl
    URL->>Analytics: POST /analytics/events
    alt analytics unavailable
      URL->>URL: fallback logs warning and continue
    end
```

## Orchestration engine sequence

```mermaid
sequenceDiagram
    participant User
    participant WF as WorkflowController
    participant Engine as WorkflowEngineService
    participant Agents as WorkflowAgents
    participant DB as Orchestrator DB

    User->>WF: POST /workflow/start
    WF->>Engine: startWorkflow()
    Engine->>DB: create execution + state STARTED
    Engine->>Agents: execute ordered agents
    Agents-->>Engine: context updates + completed states
    Engine->>DB: persist context + audit trail
    Engine-->>WF: state APPROVAL_PENDING
    WF-->>User: 201 response
```

## Workflow state machine

```mermaid
stateDiagram-v2
    [*] --> STARTED
    STARTED --> REQUIREMENT_COMPLETED
    REQUIREMENT_COMPLETED --> PLANNING_COMPLETED
    PLANNING_COMPLETED --> ARCHITECTURE_COMPLETED
    ARCHITECTURE_COMPLETED --> IMPLEMENTATION_COMPLETED
    IMPLEMENTATION_COMPLETED --> TESTING_COMPLETED
    TESTING_COMPLETED --> DOCUMENTATION_COMPLETED
    DOCUMENTATION_COMPLETED --> REVIEW_COMPLETED
    REVIEW_COMPLETED --> APPROVAL_PENDING

    APPROVAL_PENDING --> APPROVED
    APPROVAL_PENDING --> REPLANNED
    REPLANNED --> RETRY_PENDING
    RETRY_PENDING --> REQUIREMENT_COMPLETED

    STARTED --> SAFE_STOPPED
    REQUIREMENT_COMPLETED --> SAFE_STOPPED
    PLANNING_COMPLETED --> SAFE_STOPPED
    ARCHITECTURE_COMPLETED --> SAFE_STOPPED
    IMPLEMENTATION_COMPLETED --> SAFE_STOPPED
    TESTING_COMPLETED --> SAFE_STOPPED
    DOCUMENTATION_COMPLETED --> SAFE_STOPPED
    REVIEW_COMPLETED --> SAFE_STOPPED
    APPROVAL_PENDING --> SAFE_STOPPED
    REPLANNED --> SAFE_STOPPED
    RETRY_PENDING --> SAFE_STOPPED

    SAFE_STOPPED --> RETRY_PENDING
    SAFE_STOPPED --> ROLLED_BACK
    APPROVAL_PENDING --> ROLLED_BACK
    REPLANNED --> ROLLED_BACK
    RETRY_PENDING --> ROLLED_BACK
```

## Internal layering pattern

Each service follows a conventional package layout:

- controller
- service
- repository
- entity
- dto
- mapper
- exception
- config
- util/validation/common

## Operational defaults

- OpenAPI endpoint per service: /v3/api-docs
- Swagger UI per service: /swagger-ui.html
- Health endpoint per service: /internal/health
- Management endpoint exposure: health, prometheus