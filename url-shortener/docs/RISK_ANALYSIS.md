# Risk Analysis

## Summary

The platform is functional and test-green for local environments. Main risks are production hardening gaps rather than correctness blockers.

## Risk register

1. Runtime profile risk
- Description: only H2 profile is configured in repository state.
- Impact: production deployment mismatch.
- Mitigation: introduce production profile, externalized datasource secrets, and migration scripts.

2. API gateway functional gap
- Description: gateway module currently has no routing/filter configuration.
- Impact: unclear ingress path ownership in production.
- Mitigation: define gateway routes, auth filters, rate limits, and integration tests.

3. Observability depth
- Description: health and prometheus are exposed, but no centralized tracing/metrics dashboards in repo.
- Impact: slower incident diagnosis.
- Mitigation: add OpenTelemetry tracing and dashboard definitions.

4. Analytics durability gap
- Description: analytics event call failure is swallowed after warning log.
- Impact: partial analytics loss during outages.
- Mitigation: add async queue/outbox with retry and dead-letter handling.

5. Workflow orchestration scaling
- Description: orchestration currently executes in-process.
- Impact: scaling and long-running workflow contention risk.
- Mitigation: move execution to queue-backed workers and persist checkpoints with explicit locking strategy.

6. Security controls completeness
- Description: no authentication/authorization constraints on public endpoints.
- Impact: endpoint abuse and unauthorized usage risk.
- Mitigation: add OAuth2/JWT, service-to-service auth, and policy enforcement.