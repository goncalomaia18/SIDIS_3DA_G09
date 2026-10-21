
package pt.isep.sidis.flightoperations.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pt.isep.sidis.flightoperations.domain.FlightStatus;
import pt.isep.sidis.flightoperations.domain.ScheduledFlight;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduledFlightRepository
        extends JpaRepository<ScheduledFlight, Long> {

    // Verificar se existe um voo de determinada aeronave
    // com um estado específico
    boolean existsByAircraftRegistrationNumberAndStatus(
            String aircraftRegistrationNumber,
            FlightStatus status
    );

    // Procurar todos os voos de uma aeronave
    List<ScheduledFlight> findByAircraftRegistrationNumber(
            String aircraftRegistrationNumber
    );

    // US229 - Contar os voos agrupados por rota
    @Query("""
            SELECT f.routeId, COUNT(f)
            FROM ScheduledFlight f
            GROUP BY f.routeId
            ORDER BY COUNT(f) DESC
            """)
    List<Object[]> countFlightsGroupedByRoute();

    // US223 - Obter voos num intervalo de datas
    // O agrupamento por aeronave e mês será feito no Service
    List<ScheduledFlight> findByScheduledDateTimeBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    // Procurar todos os voos associados a uma rota
    List<ScheduledFlight> findByRouteId(String routeId);

    // Contar os voos associados a uma rota
    long countByRouteId(String routeId);
}
