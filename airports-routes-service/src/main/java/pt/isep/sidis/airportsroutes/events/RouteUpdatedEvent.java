package pt.isep.sidis.airportsroutes.events;

import java.time.LocalDateTime;

public record RouteUpdatedEvent(
        String routeId,
        String originAirportCode,
        String destinationAirportCode,
        Integer estimatedFlightTime,
        Double minimumRange,
        Integer minimumCapacity,
        double distanceKm,
        LocalDateTime updatedAt
) {}