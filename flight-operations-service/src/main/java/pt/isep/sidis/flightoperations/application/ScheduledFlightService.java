
package pt.isep.sidis.flightoperations.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.isep.sidis.flightoperations.application.dto.CreateScheduledFlightDTO;
import pt.isep.sidis.flightoperations.application.dto.RouteUtilizationDTO;
import pt.isep.sidis.flightoperations.application.dto.ScheduledFlightViewDTO;
import pt.isep.sidis.flightoperations.domain.ScheduledFlight;
import pt.isep.sidis.flightoperations.repositories.ScheduledFlightRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledFlightService {

    private final ScheduledFlightRepository flightRepository;

    public ScheduledFlightService(
            ScheduledFlightRepository flightRepository) {

        this.flightRepository = flightRepository;
    }

    @Transactional
    public ScheduledFlightViewDTO scheduleFlight(
            CreateScheduledFlightDTO dto) {

        LocalDateTime dateTime =
                LocalDateTime.parse(dto.scheduledDateTime());

        ScheduledFlight flight = new ScheduledFlight(
                dto.routeId(),
                dto.registrationNumber(),
                dateTime
        );

        ScheduledFlight savedFlight =
                flightRepository.save(flight);

        return toViewDTO(savedFlight);
    }

    @Transactional(readOnly = true)
    public List<ScheduledFlightViewDTO> getScheduledFlightsByAircraft(
            String registrationNumber) {

        String normalizedRegistration =
                registrationNumber.trim().toUpperCase();

        return flightRepository
                .findByAircraftRegistrationNumber(
                        normalizedRegistration)
                .stream()
                .map(this::toViewDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RouteUtilizationDTO> getRouteUtilizationReport() {

        return flightRepository
                .countFlightsGroupedByRoute()
                .stream()
                .map(result -> new RouteUtilizationDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue()
                ))
                .toList();
    }

    private ScheduledFlightViewDTO toViewDTO(
            ScheduledFlight flight) {

        return new ScheduledFlightViewDTO(
                flight.getPk(),
                flight.getRouteId(),
                flight.getAircraftRegistrationNumber(),
                flight.getScheduledDateTime().toString(),
                flight.getStatus().name()
        );
    }
}
