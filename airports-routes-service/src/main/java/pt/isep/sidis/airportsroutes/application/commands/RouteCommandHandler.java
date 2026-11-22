
package pt.isep.sidis.airportsroutes.application.commands;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.isep.sidis.airportsroutes.application.RouteService;
import pt.isep.sidis.airportsroutes.application.dto.RouteViewDTO;
import pt.isep.sidis.airportsroutes.domain.Route;
import pt.isep.sidis.airportsroutes.domain.RouteId;
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
    private final RouteService routeService;

    public RouteCommandHandler(
            RouteRepository routeRepository,
            RouteEventPublisher eventPublisher,
            RouteService routeService) {

        this.routeRepository = routeRepository;
        this.eventPublisher = eventPublisher;
        this.routeService = routeService;
    }

    public void handleUpdateRoute(
            String routeId,
            Integer estimatedFlightTime,
            Double minimumRange,
            Integer minimumCapacity) {

        Route route = routeRepository.findByRouteId(new RouteId(routeId))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Rota não encontrada com ID: " + routeId
                ));

        route.updateParameters(
                estimatedFlightTime,
                minimumRange,
                minimumCapacity
        );

        routeRepository.save(route);

        double distanceKm = route.getMinimumRange() != null
                ? route.getMinimumRange()
                : 0.0;

        eventPublisher.publishRouteUpdated(new RouteUpdatedEvent(
                route.getRouteId().id(),
                route.getOrigin().getIataCode().toString(),
                route.getDestination().getIataCode().toString(),
                route.getEstimatedFlightTime(),
                route.getMinimumRange(),
                route.getMinimumCapacity(),
                distanceKm,
                LocalDateTime.now()
        ));
    }

    public RouteViewDTO handleDeactivateRoute(String routeId) {

        RouteViewDTO deactivated = routeService.deactivateRoute(routeId);

        eventPublisher.publishRouteDeactivated(new RouteDeactivatedEvent(
                routeId,
                LocalDateTime.now()
        ));

        return deactivated;
    }
}
