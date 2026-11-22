
package pt.isep.sidis.flightoperations.application.queries;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.isep.sidis.flightoperations.application.dto.RouteUtilizationDTO;
import pt.isep.sidis.flightoperations.application.dto.ScheduledFlightViewDTO;
import pt.isep.sidis.flightoperations.readmodel.ScheduledFlightReadModel;
import pt.isep.sidis.flightoperations.repositories.ScheduledFlightReadRepository;

import java.util.List;

@Service
public class ScheduledFlightQueryHandler {

    private final ScheduledFlightReadRepository readRepository;

    public ScheduledFlightQueryHandler(
            ScheduledFlightReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    @Transactional(readOnly = true)
    public List<ScheduledFlightViewDTO> getLocalFlightsByAircraft(
            String registrationNumber) {

        return readRepository
                .findByAircraftRegistrationNumber(
                        registrationNumber.trim().toUpperCase())
                .stream()
                .map(this::toViewDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScheduledFlightViewDTO> getScheduledFlightsByAircraft(
            String registrationNumber) {

        return getLocalFlightsByAircraft(registrationNumber);
    }

    @Transactional(readOnly = true)
    public List<RouteUtilizationDTO> getRouteUtilizationReport() {

        return readRepository
                .countFlightsGroupedByRoute()
                .stream()
                .map(result -> new RouteUtilizationDTO(
                        (String) result[0],
                        ((Number) result[1]).longValue()
                ))
                .toList();
    }

    private ScheduledFlightViewDTO toViewDTO(
            ScheduledFlightReadModel flight) {

        return new ScheduledFlightViewDTO(
                flight.getId(),
                flight.getRouteId(),
                flight.getAircraftRegistrationNumber(),
                flight.getScheduledDateTime().toString(),
                flight.getStatus()
        );
    }
}
