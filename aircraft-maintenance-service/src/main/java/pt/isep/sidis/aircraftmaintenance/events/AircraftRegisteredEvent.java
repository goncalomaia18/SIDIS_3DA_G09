
package pt.isep.sidis.aircraftmaintenance.events;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AircraftRegisteredEvent(
        UUID eventId,
        String registrationNumber,
        String modelName,
        String status,
        LocalDate manufacturingDate,
        Integer seatingCapacity,
        String modelPhotoUrl,
        String features,
        String engineType,
        Instant occurredAt
) {}
