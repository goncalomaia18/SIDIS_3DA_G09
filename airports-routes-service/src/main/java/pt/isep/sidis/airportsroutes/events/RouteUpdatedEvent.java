package pt.isep.sidis.airportsroutes.events;

import java.time.LocalDateTime;

public record RouteUpdatedEvent(
        Long routeId,
        String originAirportCode,
        String destinationAirportCode,
        double distanceKm,
        LocalDateTime updatedAt
) {}