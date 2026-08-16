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


# booking-service

Owns bookings, seat reservation, and cancellation. Part of the Flight Management System
microservices project (Member B's service).

## Run locally
```bash
cd booking-service
./mvnw spring-boot:run
```
Runs on `http://localhost:8082`.

## Database
Runs on PostgreSQL (not H2) so data persists across restarts. Requires a local Postgres
container running before starting the service:
```bash
docker run --name bookingdb -e POSTGRES_USER=fms -e POSTGRES_PASSWORD=fms_password -e POSTGRES_DB=bookingdb -p 5434:5432 -d postgres:16-alpine
```
Host port `5434` is used (not the default `5432`) to avoid a clash with user-service's and
flight-service's Postgres containers. Once running, `application.properties` connects
automatically — no code changes needed after `docker run`.

## Auth
Every `/api/bookings/**` request needs header:

X-API-KEY: BOOKING-SERVICE-SECRET-KEY-2026

## Endpoints
| Method | Endpoint | Purpose |
|---|---|---|
| POST | /api/bookings | Create a booking |
| GET | /api/bookings/{id} | Get one booking |
| GET | /api/bookings/user/{userId} | All bookings for a user |
| PUT | /api/bookings/{id}/cancel | Cancel a booking (status → CANCELLED) |
| DELETE | /api/bookings/{id} | Delete a booking |

## Docs
Swagger UI: `http://localhost:8082/swagger-ui.html`

## UI
A simple frontend for testing the API is available at `booking-service-ui/index.html`
(open with VS Code Live Server, or any static file server).

## Docker
```bash
docker build -t booking-service .
docker run -p 8082:8082 booking-service
```

## Testing
Postman collection: `postman/booking-service.postman_collection.json`

## Design Note
`flightId` and `userId` in the `Booking` entity are plain numeric values, not foreign keys
into another service's database. Each microservice owns its own data — booking-service
never reaches directly into flight-service's or user-service's tables. Validating that a
flight actually exists before creating a booking would require an HTTP call from
booking-service to flight-service — a reasonable future enhancement, not required for
the current scope.