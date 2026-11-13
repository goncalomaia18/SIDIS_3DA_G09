package pt.isep.sidis.airportsroutes.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "sidis.events";
    public static final String ROUTING_KEY = "aircraft.registered";

    @Bean
    public TopicExchange sidisExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue aircraftQueue(
            @Value("${sidis.rabbitmq.aircraft.queue}") String queueName) {

        return new Queue(queueName, true);
    }

    @Bean
    public Binding aircraftBinding(
            Queue aircraftQueue,
            TopicExchange sidisExchange) {

        return BindingBuilder
                .bind(aircraftQueue)
                .to(sidisExchange)
                .with(ROUTING_KEY);
    }

    // --- Filas do Read Model de Rotas (uma por instância/porta) ---

    @Bean
    public Queue routeCreatedQueue(@Value("${server.port}") String serverPort) {
        return new Queue("airports.routes.created." + serverPort, true);
    }

    @Bean
    public Binding routeCreatedBinding(Queue routeCreatedQueue, TopicExchange sidisExchange) {
        return BindingBuilder
                .bind(routeCreatedQueue)
                .to(sidisExchange)
                .with("route.created");
    }

    @Bean
    public Queue routeUpdatedQueue(@Value("${server.port}") String serverPort) {
        return new Queue("airports.routes.updated." + serverPort, true);
    }

    @Bean
    public Binding routeUpdatedBinding(Queue routeUpdatedQueue, TopicExchange sidisExchange) {
        return BindingBuilder
                .bind(routeUpdatedQueue)
                .to(sidisExchange)
                .with("route.updated");
    }

    @Bean
    public Queue routeDeactivatedQueue(@Value("${server.port}") String serverPort) {
        return new Queue("airports.routes.deactivated." + serverPort, true);
    }

    @Bean
    public Binding routeDeactivatedBinding(Queue routeDeactivatedQueue, TopicExchange sidisExchange) {
        return BindingBuilder
                .bind(routeDeactivatedQueue)
                .to(sidisExchange)
                .with("route.deactivated");
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}