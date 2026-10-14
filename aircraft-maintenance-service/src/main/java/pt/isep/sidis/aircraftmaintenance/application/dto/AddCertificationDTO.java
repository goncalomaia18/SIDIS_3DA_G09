package pt.isep.sidis.aircraftmaintenance.application.dto;

import java.time.LocalDate;

public record AddCertificationDTO(
        String certificationNumber,
        String modelName,
        LocalDate issueDate,
        LocalDate expiryDate
) {}