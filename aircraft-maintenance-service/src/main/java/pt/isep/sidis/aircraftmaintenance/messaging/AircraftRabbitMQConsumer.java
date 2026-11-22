
package pt.isep.sidis.aircraftmaintenance.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pt.isep.sidis.aircraftmaintenance.events.AircraftRegisteredEvent;
import pt.isep.sidis.aircraftmaintenance.events.AircraftStatusUpdatedEvent;
import pt.isep.sidis.aircraftmaintenance.events.MaintenanceRecordCreatedEvent;

import pt.isep.sidis.aircraftmaintenance.readmodel.AircraftReadModel;
import pt.isep.sidis.aircraftmaintenance.readmodel.MaintenanceRecordReadModel;

import pt.isep.sidis.aircraftmaintenance.repositories.AircraftReadRepository;
import pt.isep.sidis.aircraftmaintenance.repositories.MaintenanceRecordReadRepository;
import pt.isep.sidis.aircraftmaintenance.events.MaintenanceRecordCompletedEvent;
@Component
public class AircraftRabbitMQConsumer {

    private final AircraftReadRepository aircraftReadRepository;
    private final MaintenanceRecordReadRepository maintenanceReadRepository;

    public AircraftRabbitMQConsumer(
            AircraftReadRepository aircraftReadRepository,
            MaintenanceRecordReadRepository maintenanceReadRepository) {

        this.aircraftReadRepository = aircraftReadRepository;
        this.maintenanceReadRepository = maintenanceReadRepository;
    }

    @RabbitListener(queues = "#{aircraftQueue.name}")
    @Transactional
    public void handleAircraftRegistered(AircraftRegisteredEvent event) {

        if (aircraftReadRepository.existsById(event.registrationNumber())) {
            return;
        }

        AircraftReadModel readModel = new AircraftReadModel(
                event.registrationNumber(),
                event.modelName(),
                event.status(),
                event.manufacturingDate(),
                event.seatingCapacity(),
                event.modelPhotoUrl(),
                event.features(),
                event.engineType()
        );

        aircraftReadRepository.save(readModel);

        System.out.println(
                "RABBITMQ READ MODEL - AIRCRAFT REGISTERED: "
                        + event.registrationNumber()
        );
    }

    @RabbitListener(queues = "#{aircraftStatusQueue.name}")
    @Transactional
    public void handleAircraftStatusUpdated(AircraftStatusUpdatedEvent event) {

        aircraftReadRepository.findById(event.registrationNumber())
                .ifPresent(readModel -> {

                    readModel.setStatus(event.status());

                    aircraftReadRepository.save(readModel);

                    System.out.println(
                            "RABBITMQ READ MODEL - AIRCRAFT STATUS UPDATED: "
                                    + event.registrationNumber()
                    );
                });
    }

    @RabbitListener(queues = "#{maintenanceQueue.name}")
    @Transactional
    public void handleMaintenanceRecordCreated(
            MaintenanceRecordCreatedEvent event) {

        if (maintenanceReadRepository.existsById(event.maintenanceRecordId())) {
            return;
        }

        MaintenanceRecordReadModel readModel =
                new MaintenanceRecordReadModel(
                        event.maintenanceRecordId(),
                        event.registrationNumber(),
                        event.aircraftModel(),
                        event.status(),
                        event.description(),
                        event.startDate(),
                        event.expectedDuration(),
                        event.completionNotes(),
                        event.cost(),
                        event.completionDate(),
                        event.componentCategory(),
                        event.templateId()
                );

        maintenanceReadRepository.save(readModel);

        System.out.println(
                "RABBITMQ READ MODEL - MAINTENANCE CREATED: "
                        + event.maintenanceRecordId()
        );
    }


    @RabbitListener(queues = "#{maintenanceCompletedQueue.name}")
    @Transactional
    public void handleMaintenanceRecordCompleted(
            MaintenanceRecordCompletedEvent event) {

        maintenanceReadRepository.findById(event.maintenanceRecordId())
                .ifPresent(readModel -> {

                    readModel.setStatus(event.status());
                    readModel.setCompletionNotes(event.completionNotes());
                    readModel.setCompletionDate(event.completionDate());

                    maintenanceReadRepository.save(readModel);

                    System.out.println(
                            "RABBITMQ READ MODEL - MAINTENANCE COMPLETED: "
                                    + event.maintenanceRecordId()
                    );
                });
    }


}
