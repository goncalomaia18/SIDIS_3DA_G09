
package pt.isep.sidis.aircraftmaintenance.messaging;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import pt.isep.sidis.aircraftmaintenance.events.AircraftRegisteredEvent;

@Component
public class AircraftEventHandler {

    private final AircraftEventPublisher publisher;

    public AircraftEventHandler(AircraftEventPublisher publisher) {
        this.publisher = publisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAircraftRegistered(AircraftRegisteredEvent event) {
        publisher.publishAircraftRegistered(event);
    }
}
