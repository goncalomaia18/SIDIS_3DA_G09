package pt.isep.sidis.flightoperations.application.dto;

public record ScheduledFlightViewDTO(
        Long id,
        String routeId,
        String registrationNumber,
        String scheduledDateTime,
        String status
) {}