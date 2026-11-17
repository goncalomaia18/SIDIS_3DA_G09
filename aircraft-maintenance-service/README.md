# Aircraft Maintenance Service

This service manages aircraft models, aircraft instances, maintenance templates, maintenance records, parts inventory, and event publication for the SIDIS distributed system.

## Purpose

The Aircraft Maintenance Service is responsible for:
- registering aircraft models and aircraft instances;
- updating aircraft operational status;
- scheduling and completing maintenance records;
- monitoring fleet maintenance alerts and low-stock warnings;
- publishing domain events to RabbitMQ for other services, especially Flight Operations.

## Ports and profiles

By default, the service runs on port 8081:

- HTTP: http://localhost:8081
- H2 database: ./data/aircraftdb_8081

The service is also designed to run with multiple independent instances using different ports, e.g. 8081 and 8085, each with its own H2 file database.

## Authentication

The service uses HTTP Basic Security.

Default credentials:
- username: user
- password: 123

You can override with:
- AIRCRAFT_MAINTENANCE_PASSWORD

## RabbitMQ integration

The service publishes events to the shared `sidis.events` exchange with the following routing keys:

- `aircraft.registered`
- `aircraft.status.updated`
- `maintenance.record.created`

Each instance has its own queue names, configured via:
- `sidis.rabbitmq.aircraft.queue`
- `sidis.rabbitmq.aircraft.status.queue`
- `sidis.rabbitmq.maintenance.queue`

## Main endpoints

### Aircraft models

- `POST /api/aircraft-models`
  - register an aircraft model
- `PATCH /api/aircraft-models/models/{modelName}/specifications`
  - update model specs
- `PATCH /api/aircraft-models/models/{modelName}/image`
  - update model image
- `GET /api/aircraft-models/models/top5`
  - top 5 models by flight hours

### Aircraft instances

- `POST /api/aircraft-models/instances`
  - register a specific aircraft instance
- `GET /api/aircraft-models/instances/{registrationNumber}`
  - fetch aircraft details
- `GET /api/aircraft-models/instances`
  - search aircrafts by model/status/year
- `PATCH /api/aircraft-models/instances/{registrationNumber}/status`
  - update aircraft status with a maintenance report
- `GET /api/aircraft-models/instances/{registrationNumber}/real-time-status`
  - get the current operational status
- `GET /api/aircraft-models/instances/operational-hours`
  - get operational hours for all aircraft

### Maintenance

- `POST /api/maintenance/templates`
  - create a maintenance template
- `POST /api/maintenance/records`
  - register a maintenance record
- `GET /api/maintenance/records/aircraft/{registrationNumber}`
  - aircraft maintenance history
- `PATCH /api/maintenance/records/{id}/complete`
  - complete an existing maintenance record
- `GET /api/maintenance/records/search`
  - search maintenance records by filters
- `GET /api/maintenance/alerts`
  - get maintenance alerts
- `GET /api/maintenance/parts/low-stock-alerts`
  - get low stock warnings

## Event flow

When an aircraft is registered:
1. the aircraft is stored in the domain repository;
2. `AircraftRegisteredEvent` is published;
3. the RabbitMQ event is sent to the shared exchange with routing key `aircraft.registered`;
4. the projection updates the aircraft read model.

When status changes:
1. the aircraft status is updated in the aggregate;
2. `AircraftStatusUpdatedEvent` is published;
3. the read model is updated in the projection handler.

When maintenance is registered:
1. the maintenance record is saved;
2. `MaintenanceRecordCreatedEvent` is published;
3. the read model is updated for maintenance queries.

## Local test flow

Run the service:

```bash
./mvnw spring-boot:run
```

Run tests:

```bash
./mvnw test
```

## Notes

- This service is built to integrate with Flight Operations for swaps/cancellations and real-time status validation.
- Some cross-service features are intentionally deferred with clear TODOs until the REST integration is implemented.
- H2 is configured as file-based per instance to prevent data loss between restarts and to support multi-instance deployments.

