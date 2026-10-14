package pt.isep.sidis.aircraftmaintenance.application.dto;

public record AircraftModelViewDTO(
        String modelName,
        String manufacturer,
        Integer defaultSeatingCapacity,
        Double fuelCapacity,
        Double maximumRange,
        Double cruisingSpeed,
        String modelPhotoUrl,
        String seatingConfiguration,
        String operatingHoursRange,
        String engineType
) {}