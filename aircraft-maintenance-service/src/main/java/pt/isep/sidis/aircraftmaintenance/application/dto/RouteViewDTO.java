package pt.isep.sidis.aircraftmaintenance.application.dto;

public record RouteViewDTO(
        String routeId,
        String originIata,
        String destinationIata,
        String status,
        Integer minimumCapacity
) {}