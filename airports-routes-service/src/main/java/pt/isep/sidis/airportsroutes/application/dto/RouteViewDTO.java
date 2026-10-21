package pt.isep.sidis.airportsroutes.application.dto;

public record RouteViewDTO(
        String routeId,
        String originIata,
        String destinationIata,
        String status,
        Integer minimumCapacity
) {}