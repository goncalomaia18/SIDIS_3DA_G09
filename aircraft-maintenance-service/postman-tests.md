# Aircraft Maintenance Service - Postman / curl tests

This file contains a quick set of validation requests for the aircraft-maintenance service.

## 1) Register model

    POST http://localhost:8081/api/aircraft-models
Authorization: Basic user:123
Content-Type: application/json

```json
{
  "modelName": "A320",
  "manufacturer": "AIRBUS",
  "seatingCapacity": 180,
  "fuelCapacity": 22000,
  "range": 6100,
  "speed": 871,
  "fuelConsumption": 2.9,
  "modelPhotoUrl": "https://example.com/a320.png",
  "seatingConfiguration": "3-3",
  "operatingHoursRange": "11000-16000",
  "engineType": "TURBOFAN"
}
```

## 2) Register aircraft instance

POST http://localhost:8081/api/aircraft-models/instances
Authorization: Basic user:123
Content-Type: application/json

```json
{
  "modelName": "A320",
  "registrationNumber": "CS-ABC",
  "manufacturingDate": "2020-12-10",
  "seatingCapacity": 180,
  "features": "WiFi, USB, New cabin"
}
```

## 3) Get aircraft by registration number

GET http://localhost:8081/api/aircraft-models/instances/CS-ABC
Authorization: Basic user:123

## 4) Update aircraft status

PATCH http://localhost:8081/api/aircraft-models/instances/CS-ABC/status
Authorization: Basic user:123
Content-Type: application/json

```json
{
  "status": "UNDER_MAINTENANCE",
  "report": "Routine inspection performed."
}
```

## 5) Create maintenance template

POST http://localhost:8081/api/maintenance/templates
Authorization: Basic user:123
Content-Type: application/json

```json
{
  "templateName": "A320-Check-C",
  "templateType": "PREVENTIVE",
  "flightHoursInterval": 300,
  "calendarDaysInterval": 180,
  "checklistTitle": "A320 C-Check",
  "checklistVersion": "1.0",
  "checklistItems": [
    {
      "taskDescription": "Inspect landing gear",
      "isMandatory": true
    },
    {
      "taskDescription": "Check hydraulic system",
      "isMandatory": true
    }
  ],
  "applicableModelIds": [1]
}
```

## 6) Create maintenance record

POST http://localhost:8081/api/maintenance/records
Authorization: Basic user:123
Content-Type: application/json

```json
{
  "registrationNumber": "CS-ABC",
  "templateId": 1,
  "description": "Scheduled maintenance review",
  "expectedDuration": 8,
  "componentCategory": "AIRFRAME",
  "startDate": "2026-11-17T10:00:00",
  "cost": 2500.0
}
```

## 7) Complete maintenance record

PATCH http://localhost:8081/api/maintenance/records/1/complete
Authorization: Basic user:123
Content-Type: application/json

```json
{
  "completionNotes": "Inspection concluded successfully. No defects found."
}
```

## 8) View aircraft maintenance history

GET http://localhost:8081/api/maintenance/records/aircraft/CS-ABC
Authorization: Basic user:123

## 9) Fleet maintenance alerts

GET http://localhost:8081/api/maintenance/alerts
Authorization: Basic user:123

## 10) Low stock parts

GET http://localhost:8081/api/maintenance/parts/low-stock-alerts
Authorization: Basic user:123

## curl examples

### Register aircraft

```bash
curl -u user:123 -H "Content-Type: application/json" \
  -X POST http://localhost:8081/api/aircraft-models/instances \
  -d '{
    "modelName": "A320",
    "registrationNumber": "CS-ABC",
    "manufacturingDate": "2020-12-10",
    "seatingCapacity": 180,
    "features": "WiFi, USB, New cabin"
  }'
```

### Update status

```bash
curl -u user:123 -H "Content-Type: application/json" \
  -X PATCH http://localhost:8081/api/aircraft-models/instances/CS-ABC/status \
  -d '{
    "status": "UNDER_MAINTENANCE",
    "report": "Routine inspection performed."
  }'
```

### Create maintenance record

```bash
curl -u user:123 -H "Content-Type: application/json" \
  -X POST http://localhost:8081/api/maintenance/records \
  -d '{
    "registrationNumber": "CS-ABC",
    "templateId": 1,
    "description": "Scheduled maintenance review",
    "expectedDuration": 8,
    "componentCategory": "AIRFRAME",
    "startDate": "2026-11-17T10:00:00",
    "cost": 2500.0
  }'
```

