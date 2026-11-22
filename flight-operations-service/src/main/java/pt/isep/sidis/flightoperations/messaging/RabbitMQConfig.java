
package pt.isep.sidis.flightoperations.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "sidis.events";

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
            @Qualifier("aircraftQueue") Queue aircraftQueue,
            TopicExchange sidisExchange) {

        return BindingBuilder
                .bind(aircraftQueue)
                .to(sidisExchange)
                .with("aircraft.registered");
    }

    @Bean
    public Queue routeQueue(
            @Value("${sidis.rabbitmq.route.queue}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding routeBinding(
            @Qualifier("routeQueue") Queue routeQueue,
            TopicExchange sidisExchange) {

        return BindingBuilder
                .bind(routeQueue)
                .to(sidisExchange)
                .with("route.created");
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }


    @Bean
    public Queue scheduledFlightQueue(
            @Value("${sidis.rabbitmq.flight.queue}") String queueName) {

        return new Queue(queueName, true);
    }

    @Bean
    public Binding scheduledFlightBinding(
            @Qualifier("scheduledFlightQueue") Queue scheduledFlightQueue,
            TopicExchange sidisExchange) {

        return BindingBuilder
                .bind(scheduledFlightQueue)
                .to(sidisExchange)
                .with("flight.scheduled.created");
    }




}
