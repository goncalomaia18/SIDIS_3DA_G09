package pt.isep.sidis.airportsroutes.application.queries;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.isep.sidis.airportsroutes.domain.Route;
import pt.isep.sidis.airportsroutes.repositories.RouteRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RouteQueryHandler {

    private final RouteRepository routeRepository;

    public RouteQueryHandler(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public List<Route> handleFindAllRoutes() {
        return routeRepository.findAll();
    }

    public Optional<Route> handleFindRouteById(Long id) {
        return routeRepository.findById(id);
    }
}