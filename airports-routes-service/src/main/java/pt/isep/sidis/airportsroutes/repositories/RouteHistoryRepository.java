package pt.isep.sidis.airportsroutes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.isep.sidis.airportsroutes.domain.RouteHistory;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteHistoryRepository extends JpaRepository<RouteHistory, Long> {


    List<RouteHistory> findByRouteIdOrderByStartDateDesc(String routeId);


    Optional<RouteHistory> findFirstByRouteIdAndEndDateIsNull(String routeId);
}