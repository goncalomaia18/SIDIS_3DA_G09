
package pt.isep.sidis.aircraftmaintenance.events;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record MaintenanceRecordCompletedEvent(
        UUID eventId,
        Long maintenanceRecordId,
        String registrationNumber,
        String status,
        String completionNotes,
        LocalDateTime completionDate,
        Instant occurredAt
) {
}
