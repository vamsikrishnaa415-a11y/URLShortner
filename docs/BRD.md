# Business Requirements Document (BRD)

## 1. Requirement Understanding

The project goal is to deliver a production-grade URL Shortener platform that transforms long URLs into short, shareable aliases and reliably redirects users to original destinations.

The platform must be designed and delivered using enterprise engineering standards:

1. Milestone-based delivery with human approval gates.
2. Strong architecture boundaries (Clean Architecture and DDD-aligned modeling).
3. Operational readiness (monitoring, containerization, CI).
4. Quality engineering with automated tests and validation.

The required technical ecosystem is fixed:

1. Java 21 and Spring Boot 3 for backend services.
2. PostgreSQL for durable data persistence.
3. Redis for low-latency caching.
4. Kafka for event-driven processing.
5. Maven for build and dependency management.
6. JUnit 5, Mockito, and TestContainers for testing.
7. OpenAPI/Swagger for API contracts.
8. Micrometer and Prometheus for observability.
9. Docker and GitHub Actions for packaging and CI.

## 2. Functional Requirements

1. Create short URL from a valid long URL.
2. Resolve short URL and perform HTTP redirect to original URL.
3. Support configurable link expiration and status validation during redirect.
4. Capture click events for each redirect request.
5. Publish click and link lifecycle events to Kafka.
6. Return link metadata and statistics through dedicated APIs.
7. Use Redis cache for high-frequency read path optimization.
8. Persist source-of-truth link data and analytics aggregates in PostgreSQL.
9. Provide API schema and examples using OpenAPI.
10. Expose health and operational telemetry endpoints.

## 3. Non Functional Requirements

1. Availability: redirect and core API endpoints should remain available during high read traffic.
2. Performance: redirect path should maintain low latency through cache-first lookup strategy.
3. Scalability: application must support horizontal scale for API and event consumers.
4. Reliability: link mapping consistency must be preserved in PostgreSQL.
5. Security: input URLs must be validated and sanitized; unsafe redirect behavior must be prevented.
6. Observability: metrics for throughput, latency, error rates, cache hit ratio, and event lag must be available.
7. Maintainability: codebase must follow SOLID and clean layering for independent evolution.
8. Testability: automated tests must include unit, integration, and container-based environment validation.
9. Deployability: service must be containerized and CI-ready.

## 4. Stakeholders

1. Product Owner: defines business priorities, accepts deliverables, approves phase gates.
2. End Users: create and consume short links.
3. Platform Engineering/DevOps: owns deployment pipeline, runtime reliability, and infrastructure standards.
4. QA/Quality Engineering: defines and enforces quality gates and test strategy.
5. Security/Compliance: validates safe redirect behavior and data handling controls.
6. Data/Analytics Consumers: use clickstream and statistics for reporting.
7. Engineering Team: designs, builds, tests, and operates the platform.

## 5. Assumptions

1. PostgreSQL, Redis, and Kafka environments are available in target runtime.
2. Consumers accept REST-based API interactions.
3. Link ownership/authentication model is out of immediate scope unless explicitly added later.
4. Redirect volume is read-heavy relative to write volume.
5. Prometheus-compatible metrics scraping is acceptable for monitoring integration.
6. Deployment targets support Docker images and GitHub Actions workflow execution.

## 6. Ambiguities

1. Required level of custom alias support is not explicitly specified.
2. Expected redirect HTTP status strategy (301 vs 302 defaults) is not finalized.
3. Retention policy for click events and analytics history is not defined.
4. Rate limiting and abuse prevention requirements are not quantified.
5. Data privacy constraints for IP/user-agent storage in analytics are not specified.
6. Multi-tenant isolation requirements are not explicitly stated.

## 7. Risks

1. Scope expansion risk from undefined analytics depth and reporting needs.
2. Integration risk across PostgreSQL, Redis, Kafka, and metrics tooling.
3. Operational risk if observability baselines are delayed.
4. Security risk from open redirect abuse and malicious URL payloads.
5. Data growth risk from unbounded event storage and analytics aggregation.
6. Delivery risk if phase approvals are delayed or skipped.

## 8. Questions for Product Owner

1. Should custom aliases be supported at launch, and are there reserved keyword policies?
2. What is the default redirect type requirement: temporary (302/307) or permanent (301/308)?
3. What analytics dimensions are mandatory at MVP: total clicks, unique clicks, geo, referrer, device?
4. What is the expected data retention period for click events and aggregates?
5. Is user authentication/authorization required for link creation and analytics access in initial release?
6. Are there compliance constraints for storing IP address or user-agent metadata?
7. What are expected peak RPS and latency targets for redirect endpoints?
8. Is support for bulk URL creation required in MVP?
9. Should expired links return a specific response body and status contract?
10. Are deletion, archival, and restore workflows required for links?

## 9. Acceptance Criteria

1. Business requirements are documented and approved by Product Owner.
2. Functional scope is explicit and mapped to measurable outcomes.
3. Non-functional constraints are defined for performance, reliability, security, and observability.
4. Known ambiguities and risks are documented with owner-facing questions.
5. In-scope and out-of-scope boundaries are clear enough to avoid uncontrolled delivery drift.
6. Success metrics and engineering goals are defined for post-implementation validation.

## 10. Scope

1. Core link creation and redirect capabilities.
2. Link expiration and validity enforcement.
3. Click event capture and event publication to Kafka.
4. Read APIs for link metadata and statistics.
5. Cache-enabled redirect optimization.
6. OpenAPI-based API documentation.
7. Observability baseline with Micrometer and Prometheus.
8. Docker packaging and CI workflow definition.

## 11. Out of Scope

1. Frontend web application and custom UX portal.
2. Enterprise SSO or advanced IAM integrations.
3. Billing/subscription management.
4. QR code generation and campaign marketing suite.
5. Global CDN edge optimization policy design.
6. Mobile SDK development.
7. Multi-region disaster recovery implementation details.

## 12. Success Metrics

1. Link creation success rate.
2. Redirect success rate.
3. Redirect p95 latency.
4. Cache hit ratio on redirect path.
5. Kafka event publication success ratio.
6. Error rate by endpoint category.
7. API contract conformance against OpenAPI.
8. Automated test pass rate in CI.

## 13. Engineering Goals

1. Deliver maintainable architecture with explicit domain boundaries.
2. Achieve reliable, low-latency redirect behavior under read-heavy load.
3. Ensure test coverage for critical domain rules and infrastructure adapters.
4. Establish reproducible local and CI execution using containers.
5. Provide actionable telemetry for runtime diagnosis and capacity planning.
6. Enforce milestone-based delivery with approval checkpoints.

## 14. Business Goals

1. Enable dependable short-link generation and sharing for business and end-user use cases.
2. Improve engagement visibility through click analytics.
3. Reduce operational risk through standardized engineering and release practices.
4. Build a scalable foundation for future monetization or advanced link management capabilities.
5. Increase delivery predictability with phase-gated execution and human approval governance.