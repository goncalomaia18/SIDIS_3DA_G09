package pt.isep.sidis.aircraftmaintenance.events;

import java.time.Instant;
import java.util.UUID;

public record AircraftRegisteredEvent(
        UUID eventId,
        String registrationNumber,
        String modelName,
        String status,
        Instant occurredAt
) {
}
