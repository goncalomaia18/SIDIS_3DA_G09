
package pt.isep.sidis.flightoperations.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pt.isep.sidis.flightoperations.readmodel.ScheduledFlightReadModel;

import java.util.List;
import java.util.UUID;

public interface ScheduledFlightReadRepository
        extends JpaRepository<ScheduledFlightReadModel, UUID> {

    List<ScheduledFlightReadModel> findByAircraftRegistrationNumber(
            String registrationNumber
    );

    @Query("""
            SELECT f.routeId, COUNT(f)
            FROM ScheduledFlightReadModel f
            GROUP BY f.routeId
            ORDER BY COUNT(f) DESC
            """)
    List<Object[]> countFlightsGroupedByRoute();



}
