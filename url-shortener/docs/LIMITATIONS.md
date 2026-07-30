# Limitations

1. Persistence profile
- Uses H2 in-memory databases only in current repository state.
- Data is not durable across restarts.

2. Gateway capability
- api-gateway currently exposes health/openapi baseline but no explicit routing policy.

3. Analytics reliability model
- Redirect flow does not fail when analytics persistence fails.
- This preserves user redirection but can lose some analytics events.

4. Orchestrator execution model
- Agent execution is in-process and sequential.
- No queue-backed distribution or workload isolation.

5. Coverage publication
- Tests pass, but there is no built-in JaCoCo publication or threshold gating.

6. Security envelope
- No built-in authentication, authorization, or rate limits in current scope.