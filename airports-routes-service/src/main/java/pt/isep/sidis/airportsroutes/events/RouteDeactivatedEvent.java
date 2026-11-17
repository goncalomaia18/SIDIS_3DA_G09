package pt.isep.sidis.airportsroutes.events;

import java.time.LocalDateTime;

public record RouteDeactivatedEvent(
        Long routeId,
        LocalDateTime deactivatedAt
) {}