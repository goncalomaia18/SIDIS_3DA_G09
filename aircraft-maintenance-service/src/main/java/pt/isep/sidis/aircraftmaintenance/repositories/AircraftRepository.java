
package pt.isep.sidis.aircraftmaintenance.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pt.isep.sidis.aircraftmaintenance.application.dto.TopAircraftModelDTO;
import pt.isep.sidis.aircraftmaintenance.domain.Aircraft;
import pt.isep.sidis.aircraftmaintenance.domain.AircraftStatus;
import pt.isep.sidis.aircraftmaintenance.domain.ModelName;
import pt.isep.sidis.aircraftmaintenance.domain.RegistrationNumber;

import java.util.List;
import java.util.Optional;

public interface AircraftRepository extends JpaRepository<Aircraft, Long> {

    Optional<Aircraft> findByRegistrationNumber(RegistrationNumber registrationNumber);

    List<Aircraft> findByStatus(AircraftStatus status);

    List<Aircraft> findByModel_ModelName(ModelName modelName);

    @Query("SELECT a FROM Aircraft a WHERE YEAR(a.manufacturingDate) = :year")
    List<Aircraft> findByManufacturingYear(@Param("year") int year);

    @Query("SELECT new pt.isep.sidis.aircraftmaintenance.application.dto.TopAircraftModelDTO(a.model.modelName.name, SUM(a.totalFlightHours)) " +
            "FROM Aircraft a GROUP BY a.model.modelName.name ORDER BY SUM(a.totalFlightHours) DESC")
    List<TopAircraftModelDTO> findTop5ModelsByFlightHours(Pageable pageable);

    @Query("SELECT new pt.isep.sidis.aircraftmaintenance.application.dto.TopAircraftModelDTO(a.registrationNumber.number, a.totalFlightHours) " +
            "FROM Aircraft a ORDER BY a.totalFlightHours DESC")
    List<TopAircraftModelDTO> findTop5AircraftByFlightHours(Pageable pageable);

    @Query("SELECT a FROM Aircraft a WHERE " +
            "(:engineType IS NULL OR a.model.engineType = :engineType) AND " +
            "(:feature IS NULL OR a.features LIKE %:feature%)")
    List<Aircraft> findByFeaturesAndEngine(
            @Param("feature") String feature,
            @Param("engineType") String engineType
    );
}
