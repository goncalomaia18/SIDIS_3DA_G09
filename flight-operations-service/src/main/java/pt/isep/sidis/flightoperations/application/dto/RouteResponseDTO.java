package pt.isep.sidis.flightoperations.application.dto;

public record RouteResponseDTO(
        String routeId,
        String originIata,
        String destinationIata,
        String status,
        Integer minimumCapacity,
        Double minimumRange,
        String originAirportStatus,
        String destinationAirportStatus
) {
}
