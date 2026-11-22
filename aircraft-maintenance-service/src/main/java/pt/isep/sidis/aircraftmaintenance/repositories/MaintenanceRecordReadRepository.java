
package pt.isep.sidis.aircraftmaintenance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import pt.isep.sidis.aircraftmaintenance.readmodel.MaintenanceRecordReadModel;

import java.util.List;

public interface MaintenanceRecordReadRepository
        extends JpaRepository<MaintenanceRecordReadModel, Long>,
        JpaSpecificationExecutor<MaintenanceRecordReadModel> {

    List<MaintenanceRecordReadModel>
    findByRegistrationNumberOrderByStartDateDesc(String registrationNumber);
}
