package pt.isep.sidis.airportsroutes.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pt.isep.sidis.airportsroutes.events.RouteCreatedEvent;
import pt.isep.sidis.airportsroutes.events.RouteDeactivatedEvent;
import pt.isep.sidis.airportsroutes.events.RouteUpdatedEvent;
import pt.isep.sidis.airportsroutes.readmodel.RouteReadModel;
import pt.isep.sidis.airportsroutes.repositories.RouteReadRepository;

@Component
public class RouteEventHandler {

    private final RouteEventPublisher publisher;
    private final RouteReadRepository routeReadRepository;

    public RouteEventHandler(RouteEventPublisher publisher, RouteReadRepository routeReadRepository) {
        this.publisher = publisher;
        this.routeReadRepository = routeReadRepository;
    }

    // 1. Mantém a publicação do evento de domínio após commit da escrita
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRouteCreated(RouteCreatedEvent event) {
        publisher.publishRouteCreated(event);
    }


    @RabbitListener(queues = "#{routeCreatedQueue.name}")
    @Transactional
    public void handleRouteCreated(RouteCreatedEvent event) {

        RouteReadModel readModel = new RouteReadModel(
                event.routeId(),
                event.originIata(),
                event.destinationIata(),
                event.estimatedFlightTime(),
                event.minimumRange(),
                event.minimumCapacity(),
                event.status() != null ? event.status() : "ACTIVE"
        );

        readModel.setOriginAirportStatus(
                event.originAirportStatus()
        );

        readModel.setDestinationAirportStatus(
                event.destinationAirportStatus()
        );

        routeReadRepository.save(readModel);

        System.out.println(
                "READ MODEL UPDATED - ROUTE CREATED: "
                        + event.routeId()
        );
    }



    // 3. Consumidor RabbitMQ: atualiza o Read Model quando a rota é alterada

    @RabbitListener(queues = "#{routeUpdatedQueue.name}")
    @Transactional
    public void handleRouteUpdated(RouteUpdatedEvent event) {

        routeReadRepository.findById(event.routeId())
                .ifPresent(readModel -> {

                    readModel.setOriginIata(
                            event.originAirportCode()
                    );

                    readModel.setDestinationIata(
                            event.destinationAirportCode()
                    );

                    readModel.setEstimatedFlightTime(
                            event.estimatedFlightTime()
                    );

                    readModel.setMinimumRange(
                            event.minimumRange()
                    );

                    readModel.setMinimumCapacity(
                            event.minimumCapacity()
                    );

                    routeReadRepository.save(readModel);

                    System.out.println(
                            "READ MODEL UPDATED - ROUTE UPDATED: "
                                    + event.routeId()
                    );
                });
    }



    @RabbitListener(queues = "#{routeDeactivatedQueue.name}")
    @Transactional
    public void handleRouteDeactivated(RouteDeactivatedEvent event) {

        routeReadRepository.findById(event.routeId())
                .ifPresent(readModel -> {

                    readModel.setStatus("INACTIVE");

                    routeReadRepository.save(readModel);

                    System.out.println(
                            "READ MODEL UPDATED - ROUTE DEACTIVATED: "
                                    + event.routeId()
                    );
                });
    }

}