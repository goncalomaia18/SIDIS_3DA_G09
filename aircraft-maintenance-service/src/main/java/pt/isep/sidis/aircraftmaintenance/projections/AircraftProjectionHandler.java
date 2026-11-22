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

        AircraftReadModel readModel = new AircraftReadModel(
                event.registrationNumber(),
                event.modelName(),
                event.status(),
                event.manufacturingDate(),
                event.seatingCapacity(),
                event.modelPhotoUrl(),
                event.features(),
                event.engineType()
        );

        repository.save(readModel);

        System.out.println(
                "READ MODEL UPDATED - AIRCRAFT CREATED: "
                        + event.registrationNumber()
        );
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