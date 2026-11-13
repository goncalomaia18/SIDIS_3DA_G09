package pt.isep.sidis.airportsroutes.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.isep.sidis.airportsroutes.readmodel.RouteReadModel;

import java.util.List;

@Repository
public interface RouteReadRepository extends JpaRepository<RouteReadModel, String> {

    List<RouteReadModel> findByStatus(String status);

    Page<RouteReadModel> findByOriginIata(String originIata, Pageable pageable);

    Page<RouteReadModel> findByDestinationIata(String destinationIata, Pageable pageable);

    Page<RouteReadModel> findByOriginIataAndDestinationIata(String originIata, String destinationIata, Pageable pageable);

    Page<RouteReadModel> findByOriginIataOrDestinationIata(String originIata, String destinationIata, Pageable pageable);
}