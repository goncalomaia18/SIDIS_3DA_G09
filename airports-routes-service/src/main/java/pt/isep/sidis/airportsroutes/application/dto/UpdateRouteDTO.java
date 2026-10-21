package pt.isep.sidis.airportsroutes.application.dto;

import jakarta.validation.constraints.Min;

public record UpdateRouteDTO(
        @Min(value = 1, message = "Flight time must be positive")
        Integer estimatedFlightTime,

        @Min(value = 1, message = "Range must be positive")
        Double minimumRange,

        @Min(value = 1, message = "Capacity must be positive")
        Integer minimumCapacity
) {}