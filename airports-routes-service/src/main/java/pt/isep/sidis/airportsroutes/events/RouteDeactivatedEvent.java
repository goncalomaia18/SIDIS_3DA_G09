
package pt.isep.sidis.airportsroutes.events;

import java.time.LocalDateTime;

public record RouteDeactivatedEvent(
        String routeId,
        LocalDateTime deactivatedAt
) {}
