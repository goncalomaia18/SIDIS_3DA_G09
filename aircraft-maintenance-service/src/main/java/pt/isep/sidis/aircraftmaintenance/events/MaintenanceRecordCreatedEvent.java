package pt.isep.sidis.aircraftmaintenance.events;

import java.time.Instant;
import java.util.UUID;

public record MaintenanceRecordCreatedEvent(
        UUID eventId,
        Long maintenanceRecordId,
        String registrationNumber,
        String aircraftModel,
        String status,
        Instant occurredAt
) {
}

