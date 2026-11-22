
package pt.isep.sidis.aircraftmaintenance.events;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record MaintenanceRecordCreatedEvent(
        UUID eventId,
        Long maintenanceRecordId,
        String registrationNumber,
        String aircraftModel,
        String status,
        String description,
        LocalDateTime startDate,
        Integer expectedDuration,
        String completionNotes,
        Double cost,
        LocalDateTime completionDate,
        String componentCategory,
        Long templateId,
        Instant occurredAt
) {
}
