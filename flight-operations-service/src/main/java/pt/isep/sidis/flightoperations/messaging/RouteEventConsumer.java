
package pt.isep.sidis.flightoperations.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pt.isep.sidis.flightoperations.events.RouteCreatedEvent;

@Component
public class RouteEventConsumer {

    @RabbitListener(queues = "${sidis.rabbitmq.route.queue}")
    public void onRouteCreated(RouteCreatedEvent event) {

        System.out.println("==================================");
        System.out.println("RABBITMQ - ROUTE CREATED RECEIVED");
        System.out.println("Event ID: " + event.eventId());
        System.out.println("Route ID: " + event.routeId());
        System.out.println("Origin: " + event.originIata());
        System.out.println("Destination: " + event.destinationIata());
        System.out.println("Status: " + event.status());
        System.out.println("==================================");
    }
}
