
package pt.isep.sidis.airportsroutes.events;

import java.time.Instant;
import java.util.UUID;

public record RouteCreatedEvent(
        UUID eventId,
        String routeId,
        String originIata,
        String destinationIata,
        Integer estimatedFlightTime,
        Double minimumRange,
        Integer minimumCapacity,
        String originAirportStatus,
        String destinationAirportStatus,
        String status,
        Instant occurredAt
) {}
