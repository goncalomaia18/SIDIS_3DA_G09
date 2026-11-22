
package pt.isep.sidis.flightoperations.application.dto;

import java.util.UUID;

public record ScheduledFlightViewDTO(
        UUID id,
        String routeId,
        String registrationNumber,
        String scheduledDateTime,
        String status
) {
}
