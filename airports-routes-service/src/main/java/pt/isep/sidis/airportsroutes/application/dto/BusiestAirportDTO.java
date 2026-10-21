package pt.isep.sidis.airportsroutes.application.dto;

public record BusiestAirportDTO(
        String iataCode,
        String name,
        long routeCount
) {}