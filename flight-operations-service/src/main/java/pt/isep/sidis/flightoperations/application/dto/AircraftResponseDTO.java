
package pt.isep.sidis.flightoperations.application.dto;

import java.time.LocalDate;

public record AircraftResponseDTO(
        String registrationNumber,
        String modelName,
        LocalDate manufacturingDate,
        Integer seatingCapacity,
        String status,
        String modelPhotoUrl,
        String features,
        String engineType
) {}
