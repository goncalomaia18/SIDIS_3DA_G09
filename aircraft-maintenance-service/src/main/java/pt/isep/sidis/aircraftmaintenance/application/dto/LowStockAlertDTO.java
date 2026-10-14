package pt.isep.sidis.aircraftmaintenance.application.dto;

public record LowStockAlertDTO(
        String partNumber,
        String partName,
        Integer currentStock,
        Integer minimumThreshold,
        String alertMessage
) {}