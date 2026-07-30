# Trade-offs

## 1. Simplicity over distributed complexity

- Decision: separate services but keep synchronous communication for URL to analytics.
- Benefit: low operational complexity and predictable debugging.
- Cost: tighter runtime coupling and latency sensitivity.

## 2. H2 in-memory databases for local velocity

- Decision: default to H2 in each module.
- Benefit: zero external infrastructure required for development and tests.
- Cost: non-production persistence profile and differences from real RDBMS behavior.

## 3. Lightweight orchestration model

- Decision: implement agentic workflow in-process with persisted state and audit entries.
- Benefit: transparent behavior, easier reasoning, small dependency surface.
- Cost: no distributed worker pool, no external queue durability, limited horizontal workflow execution controls.

## 4. Manual API documentation and contract-first annotations

- Decision: use Swagger annotations plus markdown docs.
- Benefit: clear API discoverability.
- Cost: risk of drift if docs are not enforced in CI checks.

## 5. Fallback-first resilience for analytics recording

- Decision: redirect path continues when analytics call fails.
- Benefit: user-facing redirect reliability remains high.
- Cost: possible analytics event loss during downstream outages.