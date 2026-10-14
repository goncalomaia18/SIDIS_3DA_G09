package pt.isep.sidis.aircraftmaintenance.application.dto;

public record ChecklistItemDTO(
        String taskDescription,
        Boolean isMandatory
) {}