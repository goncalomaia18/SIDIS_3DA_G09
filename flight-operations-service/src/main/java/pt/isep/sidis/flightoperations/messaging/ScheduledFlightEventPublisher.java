
package pt.isep.sidis.flightoperations.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import pt.isep.sidis.flightoperations.events.ScheduledFlightCreatedEvent;

@Component
public class ScheduledFlightEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public ScheduledFlightEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishScheduledFlightCreated(
            ScheduledFlightCreatedEvent event) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                "flight.scheduled.created",
                event
        );

        System.out.println(
                "RABBITMQ - SCHEDULED FLIGHT CREATED PUBLISHED: "
                        + event.flightId()
        );
    }
}
