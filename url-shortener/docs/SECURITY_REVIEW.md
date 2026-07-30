# Security Review

## Scope

- URL service API
- Analytics service API
- Orchestrator service API
- Shared exception and validation patterns

## Positive controls already present

- Jakarta validation on request DTOs and path/query parameters.
- Centralized exception handlers returning structured error payloads.
- Correlation ID propagation in error responses when available.
- No direct dynamic SQL usage; JPA repository abstraction is used.

## Gaps and recommendations

1. Authentication and authorization
- Current state: not implemented.
- Recommendation: enforce OAuth2 resource server with JWT and role-based endpoint policies.

2. Transport security
- Current state: no TLS termination settings in repo.
- Recommendation: enforce HTTPS at ingress and mTLS for internal traffic in production.

3. URL abuse protection
- Current state: no explicit allow/deny list and no SSRF-oriented domain policy.
- Recommendation: add URL normalization plus allowlist/denylist policies and DNS/IP safety checks.

4. Rate limiting and abuse control
- Current state: not configured in gateway or services.
- Recommendation: add client rate limiting, burst control, and bot protection.

5. Sensitive logging review
- Current state: structured errors are safe, but request payload logging policy is not explicit.
- Recommendation: add log redaction policy and tests for sensitive fields.

6. Secrets and config management
- Current state: development defaults only.
- Recommendation: move secrets to secret manager and inject at runtime.

## Security readiness verdict

- Development readiness: acceptable.
- Production readiness: conditional on authn/authz, TLS, abuse controls, and secrets hardening.