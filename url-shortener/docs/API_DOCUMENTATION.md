# API Documentation

## Base URLs

- api-gateway: http://localhost:8080
- url-service: http://localhost:8081
- analytics-service: http://localhost:8082
- orchestrator-service: http://localhost:8083

## Shared endpoints

- Health: GET /internal/health
- OpenAPI JSON: GET /v3/api-docs
- Swagger UI: GET /swagger-ui.html

## URL Service

### POST /api/v1/urls

- Purpose: Create a short URL.
- Request body fields:
  - originalUrl (required, URL format, max 2048)
  - customAlias (optional, [A-Za-z0-9_-], max 64)
  - expiryDate (optional, ISO timestamp)
- Success: 201 Created
- Error codes: 400, 409

Example request:

```json
{
  "originalUrl": "https://example.com/articles/abc",
  "customAlias": "example-abc",
  "expiryDate": "2026-12-31T23:59:59Z"
}
```

### GET /api/v1/urls/{shortCode}

- Purpose: Fetch short URL details by short code.
- Path constraints: min length 4, max length 64.
- Success: 200 OK
- Error codes: 404

### PUT /api/v1/urls/{id}

- Purpose: Update original URL, alias, expiry, or active flag.
- Path constraints: id > 0.
- Success: 200 OK
- Error codes: 400, 404, 409

### DELETE /api/v1/urls/{id}

- Purpose: Delete URL mapping.
- Path constraints: id > 0.
- Success: 204 No Content
- Error codes: 404

### GET /r/{shortCode}

- Purpose: Resolve and redirect to original URL.
- Redirect status: 302 Found with Location header.
- Side effect: attempts async-style fire-and-forget analytics tracking call.
- Error codes: 404, 410

## Analytics Service

### POST /analytics/events

- Purpose: Persist redirect event.
- Request body fields:
  - shortCode (required, max 32)
  - clickedAt (required, ISO timestamp)
  - ipAddress (required, max 45)
  - browser (required, max 128)
  - device (required, max 128)
  - operatingSystem (required, max 128)
  - referrer (optional, max 512)
- Success: 201 Created
- Error codes: 400

### GET /analytics/{shortCode}

- Purpose: Return aggregate analytics for one short code.
- Success: 200 OK
- Error codes: 404

### GET /analytics/top?limit={n}

- Purpose: Return top clicked short codes.
- Query constraints: limit >= 1 (optional).
- Success: 200 OK
- Error codes: 400

### GET /analytics/daily?from={yyyy-mm-dd}&to={yyyy-mm-dd}

- Purpose: Return daily click totals in optional date range.
- Success: 200 OK
- Error codes: 400

## Orchestrator Service

### POST /workflow/start

- Purpose: Start a new workflow and execute agent chain until approval gate.
- Request body fields:
  - workflowName (required, max 128)
  - initiatedBy (required, max 64)
  - correlationId (optional, max 64)
  - initialContext (optional map)
- Success: 201 Created
- Error codes: 400

Example request:

```json
{
  "workflowName": "Release-2026-07",
  "initiatedBy": "tech-lead",
  "correlationId": "release-2026-07",
  "initialContext": {
    "ticket": "REL-1001"
  }
}
```

### GET /workflow/{id}

- Purpose: Fetch workflow detail, state, dependency graph, context entries, decisions, and audit trail.
- Path constraints: id > 0.
- Success: 200 OK
- Error codes: 404

### POST /workflow/{id}/approve

- Purpose: Approve or reject workflow at approval gate.
- Request body fields:
  - approver (required)
  - decision (required: APPROVE or REJECT)
  - comments (optional)
- Success: 200 OK
- Error codes: 400, 404

### POST /workflow/{id}/retry

- Purpose: Retry workflow from SAFE_STOPPED or REPLANNED state.
- Request body field:
  - reason (optional)
- Success: 200 OK
- Error codes: 400, 404

### POST /workflow/{id}/rollback

- Purpose: Move workflow to terminal rolled-back state.
- Request body field:
  - reason (optional)
- Success: 200 OK
- Error codes: 404

## Error response contract

Every service uses a consistent JSON payload with these fields:

- timestamp
- status
- error
- message
- path
- correlationId