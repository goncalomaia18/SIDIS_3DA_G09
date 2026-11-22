package pt.isep.sidis.aircraftmaintenance.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pt.isep.sidis.aircraftmaintenance.events.AircraftRegisteredEvent;
import pt.isep.sidis.aircraftmaintenance.events.AircraftStatusUpdatedEvent;
import pt.isep.sidis.aircraftmaintenance.events.MaintenanceRecordCreatedEvent;
import pt.isep.sidis.aircraftmaintenance.events.MaintenanceRecordCompletedEvent;

@Component
public class AircraftEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public AircraftEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishAircraftRegistered(AircraftRegisteredEvent event) {
        System.out.println("RABBITMQ - AIRCRAFT REGISTERED PUBLISHED: " + event.registrationNumber());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                "aircraft.registered",
                event
        );
    }

    public void publishAircraftStatusUpdated(AircraftStatusUpdatedEvent event) {
        System.out.println("RABBITMQ - AIRCRAFT STATUS UPDATED PUBLISHED: " + event.registrationNumber());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                "aircraft.status.updated",
                event
        );
    }

    public void publishMaintenanceRecordCreated(MaintenanceRecordCreatedEvent event) {
        System.out.println("RABBITMQ - MAINTENANCE RECORD CREATED PUBLISHED: " + event.registrationNumber());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                "maintenance.record.created",
                event
        );
    }

    public void publishMaintenanceRecordCompleted(
            MaintenanceRecordCompletedEvent event) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                "maintenance.record.completed",
                event
        );

        System.out.println(
                "RABBITMQ - MAINTENANCE COMPLETED PUBLISHED: "
                        + event.maintenanceRecordId()
        );
    }

}