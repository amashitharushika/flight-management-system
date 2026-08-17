# Flight Management System

A microservices-based flight management system built for the Service-Oriented Computing coursework. Three independently deployable Spring Boot APIs — User & Auth, Flight, and Booking — sit behind a single Spring Cloud Gateway that handles authentication (Google OAuth 2.0, Backend-for-Frontend pattern), CORS, rate limiting, and per-service API key injection. The entire system runs with one command via Docker Compose.

## Architecture

```
Browser → API Gateway (OAuth2 + Redis rate limiting, :8080) → User Service (:8083, Postgres)
                                                              → Flight Service (:8081)
                                                              → Booking Service (:8082)
```

The Gateway is the only component the client ever talks to directly. It authenticates the user against Google, holds the resulting token server-side (never exposing it to the browser — the Backend-for-Frontend pattern), and relays it plus the correct per-service API key to whichever microservice a request targets.

## Services

| Service | Port | Database | Owner |
|---|---|---|---|
| api-gateway | 8080 | — | Person C |
| user-service | 8083 | PostgreSQL | Person C |
| flight-service | 8081 | H2 / PostgreSQL | Person A |
| booking-service | 8082 | H2 / PostgreSQL | Person B |

## Prerequisites

- Docker Desktop (installed and running)
- A Google Cloud OAuth 2.0 Client ID (see Setup below)

## Setup

1. Clone the repo.
2. Create a file named `.env` in the repo root (this is git-ignored — never commit it) containing:
```
GOOGLE_CLIENT_ID=your-google-oauth-client-id
GOOGLE_CLIENT_SECRET=your-google-oauth-client-secret
```
Ask a team member for these values, or create your own OAuth client in Google Cloud Console (APIs & Services → Credentials → OAuth client ID → Web application → authorized redirect URI: `http://localhost:8080/login/oauth2/code/google`).

## Running the system

```bash
docker compose up --build
```
This starts every service, Redis (for rate limiting), and PostgreSQL (for user-service), all networked together. First run takes a few minutes while images are pulled and services are built.

## Testing it

Open a browser (not curl — this needs to follow a real OAuth redirect) and go to:
```
http://localhost:8080/api/users/1
```
You should be redirected to Google's login screen. After logging in, the request completes and reaches user-service with an authenticated session.

## API Documentation (Swagger)

- User Service: `http://localhost:8083/swagger-ui.html`
- Flight Service: `http://localhost:8081/swagger-ui.html`
- Booking Service: `http://localhost:8082/swagger-ui.html`

## API Key format

Every microservice independently enforces its own API key on top of the Gateway's OAuth2 layer (defense in depth). The Gateway injects these automatically for authenticated traffic; for direct testing of an individual service, include:
```
X-API-KEY: <service-specific-key>
```

## Test credentials

| Email | Password |
|---|---|
| (fill in a real registered test account) | (fill in) |

## Data persistence

user-service uses PostgreSQL with a named Docker volume (`user-db-data`), so data survives container restarts and rebuilds — `docker compose down && docker compose up --build` will not wipe registered users. Flight and Booking services may use H2 (in-memory) or PostgreSQL depending on each owner's choice — see their respective sections in the project report.

## Repository structure

```
flight-management-system/
├── api-gateway/
├── user-service/
├── flight-service/
├── booking-service/
├── frontend/            (client application)
├── docker-compose.yml
└── README.md
```
