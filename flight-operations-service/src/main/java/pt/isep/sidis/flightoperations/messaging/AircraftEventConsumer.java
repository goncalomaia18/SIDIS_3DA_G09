
package pt.isep.sidis.flightoperations.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pt.isep.sidis.flightoperations.events.AircraftRegisteredEvent;

@Component
public class AircraftEventConsumer {

    @RabbitListener(queues = "${sidis.rabbitmq.aircraft.queue}")
    public void onAircraftRegistered(AircraftRegisteredEvent event) {

        System.out.println("==================================");
        System.out.println("RABBITMQ - AIRCRAFT REGISTERED");
        System.out.println("Event ID: " + event.eventId());
        System.out.println("Registration: " + event.registrationNumber());
        System.out.println("Model: " + event.modelName());
        System.out.println("Status: " + event.status());
        System.out.println("==================================");
    }
}
