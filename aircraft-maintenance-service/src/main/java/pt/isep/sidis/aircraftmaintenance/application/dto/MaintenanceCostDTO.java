package pt.isep.sidis.aircraftmaintenance.application.dto;

public record MaintenanceCostDTO(
        String registrationNumber,
        Double totalCost
) {
}