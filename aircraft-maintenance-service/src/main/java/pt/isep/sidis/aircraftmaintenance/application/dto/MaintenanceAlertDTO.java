package pt.isep.sidis.aircraftmaintenance.application.dto;

public record MaintenanceAlertDTO(
        String registrationNumber,
        String alertType, // Pode ser "FLIGHT_HOURS" ou "CALENDAR_DAYS"
        String message
) {
}