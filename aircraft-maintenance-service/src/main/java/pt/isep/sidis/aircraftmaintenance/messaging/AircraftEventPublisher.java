package pt.isep.sidis.aircraftmaintenance.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pt.isep.sidis.aircraftmaintenance.events.AircraftRegisteredEvent;
import pt.isep.sidis.aircraftmaintenance.events.AircraftStatusUpdatedEvent;
import pt.isep.sidis.aircraftmaintenance.events.MaintenanceRecordCreatedEvent;

@Component
public class AircraftEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public AircraftEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishAircraftRegistered(AircraftRegisteredEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                "aircraft.registered",
                event
        );
    }

    public void publishAircraftStatusUpdated(AircraftStatusUpdatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                "aircraft.status.updated",
                event
        );
    }

    public void publishMaintenanceRecordCreated(MaintenanceRecordCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                "maintenance.record.created",
                event
        );
    }
}