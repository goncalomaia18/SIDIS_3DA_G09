package pt.isep.sidis.aircraftmaintenance.application.dto;
import java.time.LocalDate;

public record CertificationViewDTO(
        String certificationNumber,
        String modelName,
        LocalDate expiryDate
) {}