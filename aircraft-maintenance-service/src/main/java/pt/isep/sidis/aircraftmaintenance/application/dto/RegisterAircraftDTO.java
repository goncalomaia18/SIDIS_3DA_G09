package pt.isep.sidis.aircraftmaintenance.application.dto;

import java.time.LocalDate;

public record RegisterAircraftDTO(
        String registrationNumber,
        String modelName,
        LocalDate manufacturingDate,
        Integer seatingCapacity,
        String features
) {}