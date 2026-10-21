package pt.isep.sidis.airportsroutes.application.dto;

import java.util.List;

public record AirportViewDTO(
        String iataCode,
        String name,
        String city,
        String country,
        String timezone,
        String type,
        String status,
        List<RunwayDTO> runways,
        List<CertificationViewDTO> certifications,

        List<FacilityDTO> facilities,

        String imageUrl,
        String operationalHours,
        String contactInformation
) {}