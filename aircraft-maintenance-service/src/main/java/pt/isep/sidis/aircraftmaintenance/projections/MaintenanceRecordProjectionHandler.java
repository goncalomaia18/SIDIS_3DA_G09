package pt.isep.sidis.aircraftmaintenance.projections;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pt.isep.sidis.aircraftmaintenance.events.MaintenanceRecordCreatedEvent;
import pt.isep.sidis.aircraftmaintenance.readmodel.MaintenanceRecordReadModel;
import pt.isep.sidis.aircraftmaintenance.repositories.MaintenanceRecordReadRepository;

@Component
public class MaintenanceRecordProjectionHandler {

    private final MaintenanceRecordReadRepository repository;

    public MaintenanceRecordProjectionHandler(MaintenanceRecordReadRepository repository) {
        this.repository = repository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMaintenanceRecordCreated(MaintenanceRecordCreatedEvent event) {
        if (repository.existsById(event.maintenanceRecordId())) {
            return;
        }

        System.out.println("READ MODEL UPDATED - MAINTENANCE RECORD CREATED: " + event.registrationNumber());
        repository.save(
                new MaintenanceRecordReadModel(
                        event.maintenanceRecordId(),
                        event.registrationNumber(),
                        event.aircraftModel(),
                        event.status(),
                        event.description(),
                        event.startDate(),
                        event.expectedDuration(),
                        event.completionNotes(),
                        event.cost(),
                        event.completionDate(),
                        event.componentCategory(),
                        event.templateId()
                )
        );
    }
}