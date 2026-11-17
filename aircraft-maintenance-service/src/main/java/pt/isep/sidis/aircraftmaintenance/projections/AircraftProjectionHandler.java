package pt.isep.sidis.aircraftmaintenance.projections;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pt.isep.sidis.aircraftmaintenance.events.AircraftRegisteredEvent;
import pt.isep.sidis.aircraftmaintenance.events.AircraftStatusUpdatedEvent;
import pt.isep.sidis.aircraftmaintenance.readmodel.AircraftReadModel;
import pt.isep.sidis.aircraftmaintenance.repositories.AircraftReadRepository;

@Component
public class AircraftProjectionHandler {

    private final AircraftReadRepository repository;

    public AircraftProjectionHandler(AircraftReadRepository repository) {
        this.repository = repository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onAircraftRegistered(AircraftRegisteredEvent event) {
        if (repository.existsById(event.registrationNumber())) {
            return;
        }

        System.out.println("READ MODEL UPDATED - AIRCRAFT CREATED: " + event.registrationNumber());
        repository.save(new AircraftReadModel(
                event.registrationNumber(),
                event.modelName(),
                event.status()
        ));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onAircraftStatusUpdated(AircraftStatusUpdatedEvent event) {
        repository.findById(event.registrationNumber()).ifPresent(readModel -> {
            readModel.setStatus(event.status());
            System.out.println("READ MODEL UPDATED - AIRCRAFT STATUS: " + event.registrationNumber());
            repository.save(readModel);
        });
    }
}