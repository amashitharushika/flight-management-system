# flight-service

Owns flights, routes, schedules, and search. Part of the Flight Management System
microservices project (Member A's service).

## Run locally
```bash
cd flight-service
./mvnw spring-boot:run
```
Runs on `http://localhost:8081`.

## Database
Runs on PostgreSQL (not H2) so data persists across restarts. Requires a local Postgres
container running before starting the service:
```bash
docker run --name flightdb -e POSTGRES_USER=fms -e POSTGRES_PASSWORD=fms_password -e POSTGRES_DB=flightdb -p 5433:5432 -d postgres:16-alpine
```
Host port `5433` is used (not the default `5432`) to avoid a clash with user-service's Postgres
container. Once running, `application.properties` connects automatically —
no code changes needed after `docker run`.

## Auth
Every `/api/flights/**` request needs header:
```
X-API-KEY: FLIGHT-SERVICE-SECRET-KEY-2026
```

## Endpoints
| Method | Endpoint | Purpose |
|---|---|---|
| GET | /api/flights | List all flights |
| GET | /api/flights/{id} | Get one flight |
| POST | /api/flights | Create a flight |
| PUT | /api/flights/{id} | Update a flight |
| DELETE | /api/flights/{id} | Delete a flight |
| GET | /api/flights/search?origin=&destination= | Search flights |

## Docs
Swagger UI: `http://localhost:8081/swagger-ui.html`

## Testing
Postman collection: `postman/flight-service.postman_collection.json`
