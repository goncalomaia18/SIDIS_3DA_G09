package pt.isep.sidis.aircraftmaintenance.messaging;

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
    public Queue aircraftQueue(@Value("${sidis.rabbitmq.aircraft.queue}") String queueName) {
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
    public Queue aircraftStatusQueue(@Value("${sidis.rabbitmq.aircraft.status.queue}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding aircraftStatusBinding(
            @Qualifier("aircraftStatusQueue") Queue aircraftStatusQueue,
            TopicExchange sidisExchange) {
        return BindingBuilder
                .bind(aircraftStatusQueue)
                .to(sidisExchange)
                .with("aircraft.status.updated");
    }

    @Bean
    public Queue maintenanceQueue(@Value("${sidis.rabbitmq.maintenance.queue}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding maintenanceBinding(
            @Qualifier("maintenanceQueue") Queue maintenanceQueue,
            TopicExchange sidisExchange) {
        return BindingBuilder
                .bind(maintenanceQueue)
                .to(sidisExchange)
                .with("maintenance.record.created");
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}