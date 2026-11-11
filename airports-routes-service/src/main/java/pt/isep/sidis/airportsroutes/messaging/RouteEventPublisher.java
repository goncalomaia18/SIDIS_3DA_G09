
package pt.isep.sidis.airportsroutes.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pt.isep.sidis.airportsroutes.events.RouteCreatedEvent;

@Component
public class RouteEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RouteEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishRouteCreated(RouteCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                "route.created",
                event
        );

        System.out.println(
                "RABBITMQ - ROUTE CREATED PUBLISHED: "
                        + event.routeId()
        );
    }
}
