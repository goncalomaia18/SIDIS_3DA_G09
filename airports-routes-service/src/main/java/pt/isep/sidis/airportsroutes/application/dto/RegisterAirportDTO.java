package pt.isep.sidis.airportsroutes.application.dto;

import java.util.List;

public record RegisterAirportDTO(
        String iataCode,
        String name,
        String city,
        String country,
        String timezone,
        String type,
        Double latitude,
        Double longitude,
        List<RunwayDTO> runways,
        List<FacilityDTO> facilities,

        String imageUrl
) {}