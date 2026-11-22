
package pt.isep.sidis.flightoperations.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pt.isep.sidis.flightoperations.events.ScheduledFlightCreatedEvent;
import pt.isep.sidis.flightoperations.readmodel.ScheduledFlightReadModel;
import pt.isep.sidis.flightoperations.repositories.ScheduledFlightReadRepository;

@Component
public class ScheduledFlightRabbitMQConsumer {

    private final ScheduledFlightReadRepository readRepository;

    public ScheduledFlightRabbitMQConsumer(
            ScheduledFlightReadRepository readRepository) {

        this.readRepository = readRepository;
    }

    @RabbitListener(queues = "#{scheduledFlightQueue.name}")
    @Transactional
    public void handleScheduledFlightCreated(
            ScheduledFlightCreatedEvent event) {

        if (readRepository.existsById(event.flightId())) {
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
                "RABBITMQ READ MODEL - FLIGHT CREATED: "
                        + event.flightId()
        );
    }
}
