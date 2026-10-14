package pt.isep.sidis.aircraftmaintenance.application.dto;

public record TurnaroundTimeDTO(
        String aircraftModel,
        Double averageTurnaroundHours
) {
}