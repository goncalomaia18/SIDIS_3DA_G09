package pt.isep.sidis.aircraftmaintenance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pt.isep.sidis.aircraftmaintenance.domain.MaintenancePart;
import pt.isep.sidis.aircraftmaintenance.domain.PartNumber;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenancePartRepository extends JpaRepository<MaintenancePart, Long> {
    Optional<MaintenancePart> findByPartNumber(PartNumber partNumber);

    @Query("SELECT p FROM MaintenancePart p WHERE p.stockQuantity <= p.minimumThreshold")
    List<MaintenancePart> findPartsWithLowStock();
}