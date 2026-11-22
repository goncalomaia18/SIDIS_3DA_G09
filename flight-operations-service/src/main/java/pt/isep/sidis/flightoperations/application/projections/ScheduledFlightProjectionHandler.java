
package pt.isep.sidis.flightoperations.application.projections;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import pt.isep.sidis.flightoperations.events.ScheduledFlightCreatedEvent;
import pt.isep.sidis.flightoperations.readmodel.ScheduledFlightReadModel;
import pt.isep.sidis.flightoperations.repositories.ScheduledFlightReadRepository;

@Component
public class ScheduledFlightProjectionHandler {

    private final ScheduledFlightReadRepository readRepository;

    public ScheduledFlightProjectionHandler(
            ScheduledFlightReadRepository readRepository) {

        this.readRepository = readRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onScheduledFlightCreated(
            ScheduledFlightCreatedEvent event) {

        if (readRepository.existsById(event.flightId())) {
            System.out.println(
                    "CQRS - FLIGHT ALREADY EXISTS: " + event.flightId()
            );
            return;
        }

        ScheduledFlightReadModel readModel =
                new ScheduledFlightReadModel(
                        event.flightId(),
                        event.routeId(),
                        event.registrationNumber(),
                        event.scheduledDateTime(),
                        event.status()
                );

        readRepository.save(readModel);

        System.out.println(
                "CQRS - READ MODEL UPDATED: " + event.flightId()
        );
    }
}
