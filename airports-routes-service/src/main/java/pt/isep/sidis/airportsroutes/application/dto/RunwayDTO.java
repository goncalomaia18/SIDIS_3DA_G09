package pt.isep.sidis.airportsroutes.application.dto;

public record RunwayDTO(
        String name,
        Double length,
        String orientation
) {}