# Testing Guide

## Test strategy

- Unit tests for service logic.
- Web slice tests for controller contracts.
- Repository tests for persistence behavior.
- Application context smoke tests per module.

## Run all tests

From url-shortener:

```bash
mvn -q test
```

## Run one module

```bash
mvn -q -pl url-service test
mvn -q -pl analytics-service test
mvn -q -pl orchestrator-service test
mvn -q -pl api-gateway test
```

## Current baseline

Latest full-suite execution result:

- analytics-service: 13/13 passing
- api-gateway: 1/1 passing
- orchestrator-service: 13/13 passing
- url-service: 15/15 passing

Total: 42 tests, all passing.

## Coverage notes

- Repository, controller, and core service paths are exercised.
- No JaCoCo report is configured yet, so line/branch percentages are not published.
- Before production deployment, add JaCoCo thresholds and CI quality gates.