package pt.isep.sidis.aircraftmaintenance.application.dto;

import jakarta.validation.constraints.NotBlank;

public record CompleteMaintenanceDTO(

        @NotBlank(message = "Completion notes cannot be blank.")
        String completionNotes
) {}