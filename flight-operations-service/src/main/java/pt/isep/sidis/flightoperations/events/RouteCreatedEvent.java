
package pt.isep.sidis.flightoperations.events;

import java.time.Instant;
import java.util.UUID;

public record RouteCreatedEvent(
        UUID eventId,
        String routeId,
        String originIata,
        String destinationIata,
        String status,
        Instant occurredAt
) {
}
