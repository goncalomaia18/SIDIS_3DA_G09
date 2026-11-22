package pt.isep.sidis.aircraftmaintenance.messaging;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import pt.isep.sidis.aircraftmaintenance.events.AircraftRegisteredEvent;
import pt.isep.sidis.aircraftmaintenance.events.AircraftStatusUpdatedEvent;
import pt.isep.sidis.aircraftmaintenance.events.MaintenanceRecordCreatedEvent;
import pt.isep.sidis.aircraftmaintenance.events.MaintenanceRecordCompletedEvent;
@Component
public class AircraftEventHandler {

    private final AircraftEventPublisher publisher;

    public AircraftEventHandler(AircraftEventPublisher publisher) {
        this.publisher = publisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onAircraftRegistered(AircraftRegisteredEvent event) {
        publisher.publishAircraftRegistered(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onAircraftStatusUpdated(AircraftStatusUpdatedEvent event) {
        publisher.publishAircraftStatusUpdated(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMaintenanceRecordCreated(MaintenanceRecordCreatedEvent event) {
        publisher.publishMaintenanceRecordCreated(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMaintenanceRecordCompleted(
            MaintenanceRecordCompletedEvent event) {

        publisher.publishMaintenanceRecordCompleted(event);
    }

}