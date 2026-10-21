package pt.isep.sidis.flightoperations.application.dto;

public record RouteUtilizationDTO(
        String routeId,
        Long flightCount
) {}