package pt.isep.sidis.aircraftmaintenance.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pt.isep.sidis.aircraftmaintenance.domain.AircraftModel;
import pt.isep.sidis.aircraftmaintenance.domain.ModelName;
import java.util.Optional;

@Repository
public interface AircraftModelRepository extends CrudRepository<AircraftModel, Long> {


    Optional<AircraftModel> findByModelName(ModelName modelName);
}