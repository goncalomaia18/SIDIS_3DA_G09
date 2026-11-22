
package pt.isep.sidis.flightoperations.application.commands;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.isep.sidis.flightoperations.application.dto.AircraftResponseDTO;
import pt.isep.sidis.flightoperations.application.dto.CreateScheduledFlightDTO;
import pt.isep.sidis.flightoperations.application.dto.RouteResponseDTO;
import pt.isep.sidis.flightoperations.application.dto.ScheduledFlightViewDTO;
import pt.isep.sidis.flightoperations.client.AircraftClient;
import pt.isep.sidis.flightoperations.client.RouteClient;
import pt.isep.sidis.flightoperations.domain.ScheduledFlight;
import pt.isep.sidis.flightoperations.repositories.ScheduledFlightRepository;
import org.springframework.context.ApplicationEventPublisher;
import pt.isep.sidis.flightoperations.events.ScheduledFlightCreatedEvent;
import java.util.UUID;

import java.time.LocalDateTime;

@Service
public class ScheduledFlightCommandHandler {

    private final ScheduledFlightRepository flightRepository;
    private final RouteClient routeClient;
    private final AircraftClient aircraftClient;
    private final ApplicationEventPublisher eventPublisher;

    public ScheduledFlightCommandHandler(
            ScheduledFlightRepository flightRepository,
            RouteClient routeClient,
            AircraftClient aircraftClient,
            ApplicationEventPublisher eventPublisher) {

        this.flightRepository = flightRepository;
        this.routeClient = routeClient;
        this.aircraftClient = aircraftClient;
        this.eventPublisher = eventPublisher;
    }
    @Transactional
    public ScheduledFlightViewDTO scheduleFlight(CreateScheduledFlightDTO dto) {

        RouteResponseDTO route = routeClient.getRouteById(dto.routeId());

        if (route == null) {
            throw new IllegalStateException(
                    "Airports & Routes returned an empty response.");
        }

        if (!"ACTIVE".equalsIgnoreCase(route.status())) {
            throw new IllegalStateException(
                    "Cannot schedule a flight: route is not active.");
        }

        if (!"OPERATIONAL".equalsIgnoreCase(route.originAirportStatus())) {
            throw new IllegalStateException(
                    "Cannot schedule a flight: origin airport is not operational.");
        }

        if (!"OPERATIONAL".equalsIgnoreCase(route.destinationAirportStatus())) {
            throw new IllegalStateException(
                    "Cannot schedule a flight: destination airport is not operational.");
        }

        String registrationNumber =
                dto.registrationNumber().trim().toUpperCase();

        AircraftResponseDTO aircraft =
                aircraftClient.getAircraftByRegistrationNumber(registrationNumber);

        if (aircraft == null) {
            throw new IllegalStateException(
                    "Aircraft & Maintenance returned an empty response.");
        }

        if (!"ACTIVE".equalsIgnoreCase(aircraft.status())) {
            throw new IllegalStateException(
                    "Cannot schedule a flight: aircraft is not active.");
        }

        if (aircraft.seatingCapacity() == null
                || route.minimumCapacity() == null) {
            throw new IllegalStateException(
                    "Cannot validate aircraft seating capacity.");
        }

        if (aircraft.seatingCapacity() < route.minimumCapacity()) {
            throw new IllegalStateException(
                    "Cannot schedule a flight: aircraft capacity is insufficient.");
        }

        LocalDateTime dateTime =
                LocalDateTime.parse(dto.scheduledDateTime());

        if (!dateTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Scheduled flight date must be in the future.");
        }

        ScheduledFlight flight = new ScheduledFlight(
                dto.routeId(),
                registrationNumber,
                dateTime
        );

        ScheduledFlight savedFlight = flightRepository.save(flight);
        eventPublisher.publishEvent(
                new ScheduledFlightCreatedEvent(
                        UUID.randomUUID(),
                        savedFlight.getFlightUuid(),
                        savedFlight.getRouteId(),
                        savedFlight.getAircraftRegistrationNumber(),
                        savedFlight.getScheduledDateTime(),
                        savedFlight.getStatus().name()
                )
        );

        return toViewDTO(savedFlight);
    }

    private ScheduledFlightViewDTO toViewDTO(ScheduledFlight flight) {
        return new ScheduledFlightViewDTO(
                flight.getFlightUuid(),
                flight.getRouteId(),
                flight.getAircraftRegistrationNumber(),
                flight.getScheduledDateTime().toString(),
                flight.getStatus().name()
        );
    }
}
