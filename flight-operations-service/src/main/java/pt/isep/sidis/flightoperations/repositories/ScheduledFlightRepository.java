package pt.isep.sidis.flightoperations.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.isep.sidis.flightoperations.domain.*;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface ScheduledFlightRepository extends JpaRepository<ScheduledFlight, Long> {

    // 1. Corrigido: Usar a matrícula (String) em vez do objeto Aircraft inteiro
    boolean existsByAircraftRegistrationAndStatus(String aircraftRegistration, FlightStatus status);

    // 2. Corrigido: Usar a matrícula (String) em vez do objeto RegistrationNumber
    List<ScheduledFlight> findByAircraftRegistration(String aircraftRegistration);

    // ------------------------------------------------------------------------------------
    // US227: Eficiência de combustível por avião e por rota
    // ATENÇÃO (SIDIS): Num microserviço, não podes calcular isto diretamente no SQL/@Query
    // porque "estimatedFlightTime" (Routes) e "fuelConsumption" (Aircraft) estão noutras bases de dados!
    // Terás de fazer estes cálculos no ScheduledFlightService (Data Forwarding / Agregação).
    // Por isso, estas queries ficam comentadas (ou removidas) para não dar erro no Spring Boot.
    // ------------------------------------------------------------------------------------
    /*
    @Query("SELECT f.aircraftRegistration, SUM(...) FROM ScheduledFlight f GROUP BY f.aircraftRegistration")
    List<Object[]> getFuelEfficiencyPerAircraft();

    @Query("SELECT f.routeId, AVG(...) FROM ScheduledFlight f GROUP BY f.routeId")
    List<Object[]> getFuelEfficiencyPerRoute();
    */

    // 3. Corrigido: US229 - Rotas mais frequentes
    // Alterado de f.route.routeId.id para f.routeId
    @Query("SELECT f.routeId, COUNT(f) " +
            "FROM ScheduledFlight f " +
            "GROUP BY f.routeId " +
            "ORDER BY COUNT(f) DESC")
    List<Object[]> countFlightsGroupedByRoute();

    // 4. Corrigido: US223 - Utilização por avião e por mês
    // Alterado de f.aircraft.registrationNumber.number para f.aircraftRegistration
    @Query("SELECT f.aircraftRegistration, FUNCTION('DATE_FORMAT', f.scheduledDateTime, '%Y-%m'), COUNT(f) " +
            "FROM ScheduledFlight f " +
            "WHERE f.scheduledDateTime BETWEEN :start AND :end " +
            "GROUP BY f.aircraftRegistration, FUNCTION('DATE_FORMAT', f.scheduledDateTime, '%Y-%m')")
    List<Object[]> countFlightsByAircraftAndMonth(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}