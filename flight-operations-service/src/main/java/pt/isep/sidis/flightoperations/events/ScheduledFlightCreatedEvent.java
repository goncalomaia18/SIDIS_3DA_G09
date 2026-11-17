
package pt.isep.sidis.flightoperations.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduledFlightCreatedEvent(
        UUID eventId,
        Long flightId,
        String routeId,
        String registrationNumber,
        LocalDateTime scheduledDateTime,
        String status
) {
}
