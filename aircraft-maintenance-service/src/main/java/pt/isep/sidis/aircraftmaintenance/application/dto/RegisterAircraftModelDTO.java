package pt.isep.sidis.aircraftmaintenance.application.dto;

public record RegisterAircraftModelDTO(
        String modelName,
        String manufacturer,
        Integer seatingCapacity,
        Double fuelCapacity,
        Double range,
        Double speed,
        Double fuelConsumption,
        String modelPhotoUrl,
        String seatingConfiguration,
        String operatingHoursRange,
        String engineType
) {}