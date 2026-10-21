package pt.isep.sidis.airportsroutes.application.dto;
import java.time.LocalDate;

public record CertificationViewDTO(
        String certificationNumber,
        String modelName,
        LocalDate expiryDate
) {}