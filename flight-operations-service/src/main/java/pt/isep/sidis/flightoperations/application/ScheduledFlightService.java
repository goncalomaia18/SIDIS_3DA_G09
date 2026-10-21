
package pt.isep.sidis.flightoperations.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.isep.sidis.flightoperations.application.dto.AircraftResponseDTO;
import pt.isep.sidis.flightoperations.application.dto.CreateScheduledFlightDTO;
import pt.isep.sidis.flightoperations.application.dto.RouteResponseDTO;
import pt.isep.sidis.flightoperations.application.dto.RouteUtilizationDTO;
import pt.isep.sidis.flightoperations.application.dto.ScheduledFlightViewDTO;
import pt.isep.sidis.flightoperations.client.AircraftClient;
import pt.isep.sidis.flightoperations.client.RouteClient;
import pt.isep.sidis.flightoperations.domain.ScheduledFlight;
import pt.isep.sidis.flightoperations.repositories.ScheduledFlightRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledFlightService {

    private final ScheduledFlightRepository flightRepository;
    private final RouteClient routeClient;
    private final AircraftClient aircraftClient;

    public ScheduledFlightService(
            ScheduledFlightRepository flightRepository,
            RouteClient routeClient,
            AircraftClient aircraftClient) {

        this.flightRepository = flightRepository;
        this.routeClient = routeClient;
        this.aircraftClient = aircraftClient;
    }

    @Transactional
    public ScheduledFlightViewDTO scheduleFlight(
            CreateScheduledFlightDTO dto) {

        // Consultar a rota no Airports & Routes (8083)
        RouteResponseDTO route = routeClient.getRouteById(
                dto.routeId()
        );

        if (route == null) {
            throw new IllegalStateException(
                    "Airports & Routes returned an empty response."
            );
        }

        if (!"ACTIVE".equalsIgnoreCase(route.status())) {
            throw new IllegalStateException(
                    "Cannot schedule a flight: route is not active."
            );
        }

        if (!"OPERATIONAL".equalsIgnoreCase(
                route.originAirportStatus())) {
            throw new IllegalStateException(
                    "Cannot schedule a flight: origin airport is not operational."
            );
        }

        if (!"OPERATIONAL".equalsIgnoreCase(
                route.destinationAirportStatus())) {
            throw new IllegalStateException(
                    "Cannot schedule a flight: destination airport is not operational."
            );
        }

        // Consultar o avião no Aircraft & Maintenance (8081)
        String registrationNumber =
                dto.registrationNumber().trim().toUpperCase();

        AircraftResponseDTO aircraft =
                aircraftClient.getAircraftByRegistrationNumber(
                        registrationNumber
                );

        if (aircraft == null) {
            throw new IllegalStateException(
                    "Aircraft & Maintenance returned an empty response."
            );
        }

        if (!"ACTIVE".equalsIgnoreCase(aircraft.status())) {
            throw new IllegalStateException(
                    "Cannot schedule a flight: aircraft is not active."
            );
        }

        if (aircraft.seatingCapacity() == null
                || route.minimumCapacity() == null) {
            throw new IllegalStateException(
                    "Cannot validate aircraft seating capacity."
            );
        }

        if (aircraft.seatingCapacity() < route.minimumCapacity()) {
            throw new IllegalStateException(
                    "Cannot schedule a flight: aircraft capacity is insufficient."
            );
        }

        // Validar data futura
        LocalDateTime dateTime =
                LocalDateTime.parse(dto.scheduledDateTime());

        if (!dateTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Scheduled flight date must be in the future."
            );
        }

        // Guardar apenas na base de dados do Flight Operations
        ScheduledFlight flight = new ScheduledFlight(
                dto.routeId(),
                registrationNumber,
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
                .findByAircraftRegistrationNumber(normalizedRegistration)
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
