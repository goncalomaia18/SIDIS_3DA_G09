package pt.isep.sidis.aircraftmaintenance.application.dto;

public record RealTimeStatusDTO(
        String registrationNumber,
        String realTimeStatus
) {}