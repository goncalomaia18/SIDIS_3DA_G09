
package pt.isep.sidis.airportsroutes.messaging;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pt.isep.sidis.airportsroutes.events.RouteCreatedEvent;

@Component
public class RouteEventHandler {

    private final RouteEventPublisher publisher;

    public RouteEventHandler(RouteEventPublisher publisher) {
        this.publisher = publisher;
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void onRouteCreated(RouteCreatedEvent event) {
        publisher.publishRouteCreated(event);
    }
}
