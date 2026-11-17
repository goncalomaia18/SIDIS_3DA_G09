package pt.isep.sidis.aircraftmaintenance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.isep.sidis.aircraftmaintenance.readmodel.MaintenanceRecordReadModel;

public interface MaintenanceRecordReadRepository extends JpaRepository<MaintenanceRecordReadModel, Long> {
}

