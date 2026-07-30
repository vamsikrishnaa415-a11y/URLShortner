# Functional Requirements Document (FRD)

## 1. Document Purpose

This FRD translates business requirements into implementable engineering requirements for the URL Shortener platform. It defines expected system behavior, constraints, measurable targets, and acceptance conditions for engineering, QA, DevOps, and product governance.

## 2. Requirement Expansion Matrix

Each BRD functional requirement is expanded below into engineering requirements.

### FR-01: Create Short URL

1. System shall accept a valid long URL and create a unique short code.
2. System shall normalize URL input before persistence.
3. System shall reject malformed URLs with deterministic validation errors.
4. System shall support optional expiration timestamp during creation.
5. System shall persist canonical mapping in PostgreSQL as system of record.

### FR-02: Resolve Short URL and Redirect

1. System shall resolve short code to destination URL using cache-first read path.
2. System shall perform redirect only for active, non-expired links.
3. System shall return defined error response when code is unknown or inactive.
4. System shall support configurable redirect status policy.

### FR-03: Expiration and Link Status Enforcement

1. System shall evaluate expiration at request time and enforce link lifecycle state.
2. System shall treat expired links as non-resolvable for redirect.
3. System shall expose link status in metadata API responses.

### FR-04: Click Event Capture

1. System shall capture a click event for every redirect attempt outcome.
2. System shall include correlation and request context metadata in event payload.
3. System shall categorize events by success, not-found, expired, or blocked status.

### FR-05: Event Publication to Kafka

1. System shall publish click and lifecycle events to Kafka topics.
2. System shall use partition strategy that preserves ordering per short code key.
3. System shall implement retry policy for transient publish failures.
4. System shall expose failed publication metrics.

### FR-06: Link Metadata and Statistics APIs

1. System shall expose read API for link metadata including creation time, expiration, status, and destination.
2. System shall expose statistics API for total clicks and derived counters.
3. System shall provide consistent pagination and filtering for list-capable endpoints.

### FR-07: Redis Caching for Redirect Path

1. System shall cache short-code to URL resolution records in Redis.
2. System shall define TTL strategy aligned with link expiration and invalidation needs.
3. System shall fall back to PostgreSQL on cache miss.
4. System shall update cache coherently after create/update lifecycle operations.

### FR-08: PostgreSQL Persistence

1. System shall persist link mappings as authoritative records.
2. System shall persist analytics aggregates and required derived counters.
3. System shall enforce uniqueness constraints for short code identity.
4. System shall support migration-driven schema evolution.

### FR-09: OpenAPI Contract

1. System shall publish OpenAPI documentation for all public APIs.
2. System shall include request/response schemas, validation rules, and error models.
3. System shall keep API documentation versioned with implementation changes.

### FR-10: Health and Telemetry Endpoints

1. System shall expose readiness and liveness checks.
2. System shall expose Micrometer metrics compatible with Prometheus.
3. System shall include dependency health indicators for PostgreSQL, Redis, and Kafka connectivity.

## 3. User Stories

1. As a user, I want to shorten a long URL so that I can share concise links.
2. As a user, I want a short URL to redirect reliably so that recipients can reach the destination without friction.
3. As a user, I want links to expire when configured so that access can be time-bounded.
4. As a product analyst, I want click statistics so that I can measure engagement.
5. As a platform engineer, I want observability metrics so that I can detect failures and performance regressions.
6. As a security stakeholder, I want strict URL validation so that malicious redirect abuse is reduced.

## 4. Use Cases

### UC-01 Create Link

1. Actor: End User or API Consumer.
2. Preconditions: API endpoint reachable; request payload valid.
3. Main Flow:
   1. Submit long URL with optional expiration.
   2. Service validates and normalizes input.
   3. Service generates short code and persists record.
   4. Service returns short URL and metadata.
4. Alternate Flow: Validation failure returns deterministic client error.
5. Postconditions: Link stored in PostgreSQL and optionally warmed into Redis.

### UC-02 Redirect by Short Code

1. Actor: Link Visitor.
2. Preconditions: Short code exists and is active.
3. Main Flow:
   1. Visitor requests short URL.
   2. Service resolves code via Redis or PostgreSQL fallback.
   3. Service validates status and expiration.
   4. Service emits click event and redirects.
4. Alternate Flows:
   1. Code not found returns defined not-found response.
   2. Code expired returns defined expired response.
5. Postconditions: Redirect outcome captured in analytics stream.

### UC-03 Retrieve Link Analytics

1. Actor: Analyst or API Consumer.
2. Preconditions: Link exists.
3. Main Flow:
   1. Actor requests statistics endpoint.
   2. Service returns aggregate metrics.
4. Postconditions: None.

## 5. Acceptance Criteria

1. Every functional requirement FR-01 to FR-10 has at least one API or system behavior test case.
2. Validation errors are deterministic and documented.
3. Redirect behavior for active, expired, and unknown links is consistent with API contract.
4. Click events are emitted for redirect outcomes and observable through metrics.
5. Cache fallback behavior works without data inconsistency.
6. OpenAPI documentation is complete for all public endpoints.
7. Health and metrics endpoints are available and scrapeable.

## 6. Business Rules

1. A short code uniquely identifies one canonical destination record at a point in time.
2. Expired links are not eligible for redirect.
3. Inactive or blocked links are not eligible for redirect.
4. Long URL input must satisfy approved scheme and format policies.
5. Analytics counters must not decrement; corrections must be explicit compensating events.
6. System-generated timestamps use UTC.

## 7. API Expectations

1. API style: REST over HTTP.
2. Content format: JSON for requests/responses except redirect endpoint behavior.
3. Error model: standardized error body with code, message, and trace correlation identifier.
4. Idempotency:
   1. Link creation endpoint behavior must be deterministic for identical requests under defined policy.
   2. Read endpoints must be side-effect free.
5. Versioning: API versioning strategy must be explicit in path or header.
6. Documentation: all endpoints and schemas published in OpenAPI.

## 8. Analytics Requirements

1. Capture click count per short code.
2. Capture timestamp of click events.
3. Capture outcome classification for redirect attempt.
4. Support aggregate queries for total clicks and time-window summaries.
5. Ensure event payload includes identifiers needed for downstream attribution.
6. Define retention policy before production release.

## 9. Security Requirements

1. Validate URL schemes against allowlist.
2. Reject malformed or suspicious URLs.
3. Enforce input length limits to prevent abuse.
4. Prevent open redirect misuse through strict mapping resolution.
5. Protect management and analytics endpoints based on approved access model.
6. Avoid sensitive data leakage in error messages and logs.

## 10. Rate Limiting Requirements

1. Define rate limits per client identity or source IP for create and analytics endpoints.
2. Apply stricter controls to mutation endpoints than redirect endpoint.
3. Return deterministic throttling response when limit is exceeded.
4. Emit rate-limiting metrics for operational tuning.

## 11. Performance Targets

1. Redirect endpoint p95 latency target: <= 50 ms under normal load with warm cache.
2. Redirect endpoint p99 latency target: <= 120 ms under normal load.
3. Link creation endpoint p95 latency target: <= 200 ms excluding external network variance.
4. Cache hit ratio target for redirect path: >= 90 percent after warm-up.
5. Event publication success target: >= 99.9 percent with retries for transient failures.

## 12. Failure Handling Requirements

1. Cache outage: service must fall back to PostgreSQL with degraded latency and preserved correctness.
2. Kafka outage: service must apply retry/backoff and expose failure metrics; redirect path must remain functional.
3. PostgreSQL unavailability: write and non-cached reads fail with explicit service error response.
4. Validation failures: return client-error responses without side effects.
5. Unknown exceptions: return generic internal error response with correlation identifier.

## 13. Monitoring Requirements

1. Expose request rate, error rate, and latency histograms per endpoint.
2. Expose cache hit and miss metrics.
3. Expose Kafka publish success/failure and consumer lag metrics where applicable.
4. Expose dependency health checks for PostgreSQL, Redis, and Kafka.
5. Provide readiness and liveness endpoints for orchestration.

## 14. Logging Requirements

1. Structured logs with timestamp, level, service name, and correlation identifier.
2. Access logs for API requests with status code and latency.
3. Error logs must include failure class and safe diagnostic context.
4. Sensitive data must be masked or excluded from logs.
5. Log level policy must support production-safe defaults.

## 15. Audit Requirements

1. Record link lifecycle operations: create, update status, expire, delete if implemented.
2. Preserve immutable audit trail attributes: actor, action, target identifier, timestamp, outcome.
3. Ensure audit records are queryable for operational and compliance review.
4. Distinguish business events from technical logs and metrics.

## 16. Traceability to BRD

1. BRD Functional Requirement 1 maps to FR-01.
2. BRD Functional Requirement 2 maps to FR-02.
3. BRD Functional Requirement 3 maps to FR-03.
4. BRD Functional Requirement 4 maps to FR-04.
5. BRD Functional Requirement 5 maps to FR-05.
6. BRD Functional Requirement 6 maps to FR-06.
7. BRD Functional Requirement 7 maps to FR-07.
8. BRD Functional Requirement 8 maps to FR-08.
9. BRD Functional Requirement 9 maps to FR-09.
10. BRD Functional Requirement 10 maps to FR-10.