package pt.isep.sidis.airportsroutes.application.commands;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.isep.sidis.airportsroutes.domain.Route;
import pt.isep.sidis.airportsroutes.events.RouteDeactivatedEvent;
import pt.isep.sidis.airportsroutes.events.RouteUpdatedEvent;
import pt.isep.sidis.airportsroutes.messaging.RouteEventPublisher;
import pt.isep.sidis.airportsroutes.repositories.RouteRepository;

import java.time.LocalDateTime;

@Service
@Transactional
public class RouteCommandHandler {

    private final RouteRepository routeRepository;
    private final RouteEventPublisher eventPublisher;

    public RouteCommandHandler(RouteRepository routeRepository, RouteEventPublisher eventPublisher) {
        this.routeRepository = routeRepository;
        this.eventPublisher = eventPublisher;
    }

    public void handleUpdateRoute(Long routeId, Integer estimatedFlightTime, Double minimumRange, Integer minimumCapacity) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Rota não encontrada com ID: " + routeId));

        route.updateParameters(estimatedFlightTime, minimumRange, minimumCapacity);
        routeRepository.save(route);

        // Extrai os dados necessários garantindo a correspondência de tipos do RouteUpdatedEvent
        String originCode = route.getOrigin() != null ? route.getOrigin().getIataCode().toString() : "";
        String destinationCode = route.getDestination() != null ? route.getDestination().getIataCode().toString() : "";
        double distanceKm = route.getMinimumRange() != null ? route.getMinimumRange() : 0.0;

        eventPublisher.publishRouteUpdated(new RouteUpdatedEvent(
                routeId,
                originCode,
                destinationCode,
                distanceKm,
                LocalDateTime.now()
        ));
    }

    public void handleDeactivateRoute(Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Rota não encontrada com ID: " + routeId));

        route.deactivate();
        routeRepository.save(route);

        eventPublisher.publishRouteDeactivated(new RouteDeactivatedEvent(
                routeId,
                LocalDateTime.now()
        ));
    }
}