package pt.isep.sidis.aircraftmaintenance.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.isep.sidis.aircraftmaintenance.domain.AircraftModel;
import pt.isep.sidis.aircraftmaintenance.domain.MaintenanceTemplate;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceTemplateRepository extends JpaRepository<MaintenanceTemplate, Long> {

    // Se for preciso de procurar templates pelo nome
    Optional<MaintenanceTemplate> findByTemplateName(String templateName);

    List<MaintenanceTemplate> findByAppliesToContaining(AircraftModel model);

}