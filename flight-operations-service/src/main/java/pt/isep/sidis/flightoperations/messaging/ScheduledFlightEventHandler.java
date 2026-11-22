
package pt.isep.sidis.flightoperations.messaging;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import pt.isep.sidis.flightoperations.events.ScheduledFlightCreatedEvent;

@Component
public class ScheduledFlightEventHandler {

    private final ScheduledFlightEventPublisher publisher;

    public ScheduledFlightEventHandler(
            ScheduledFlightEventPublisher publisher) {
        this.publisher = publisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onScheduledFlightCreated(
            ScheduledFlightCreatedEvent event) {

        publisher.publishScheduledFlightCreated(event);
    }
}
