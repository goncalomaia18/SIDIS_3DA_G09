package pt.isep.sidis.aircraftmaintenance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.isep.sidis.aircraftmaintenance.readmodel.AircraftReadModel;

public interface AircraftReadRepository extends JpaRepository<AircraftReadModel, String> {
}

