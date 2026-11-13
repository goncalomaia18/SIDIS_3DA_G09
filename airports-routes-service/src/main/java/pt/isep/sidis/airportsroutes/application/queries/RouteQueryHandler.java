package pt.isep.sidis.airportsroutes.application.queries;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.isep.sidis.airportsroutes.readmodel.RouteReadModel;
import pt.isep.sidis.airportsroutes.repositories.RouteReadRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RouteQueryHandler {

    private final RouteReadRepository routeReadRepository;

    public RouteQueryHandler(RouteReadRepository routeReadRepository) {
        this.routeReadRepository = routeReadRepository;
    }

    public RouteReadModel handleGetRouteById(String routeId) {
        return routeReadRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Rota não encontrada no Read Model: " + routeId));
    }

    public Page<RouteReadModel> handleSearchRoutes(String origin, String destination, Pageable pageable) {
        if (origin != null && destination != null) {
            return routeReadRepository.findByOriginIataAndDestinationIata(origin, destination, pageable);
        }
        if (origin != null) {
            return routeReadRepository.findByOriginIata(origin, pageable);
        }
        if (destination != null) {
            return routeReadRepository.findByDestinationIata(destination, pageable);
        }
        return routeReadRepository.findAll(pageable);
    }

    public List<RouteReadModel> handleGetActiveRoutes() {
        return routeReadRepository.findByStatus("ACTIVE");
    }
}