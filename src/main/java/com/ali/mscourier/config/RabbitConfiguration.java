package com.ali.mscourier.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
public class RabbitConfiguration {
    @Bean
    public Declarables courierTopology(
            @Value("${courier.messaging.exchange:order.exchange}") String exchangeName,
            @Value("${courier.messaging.queue:courier.state.events}") String queueName) {
        TopicExchange exchange = new TopicExchange(exchangeName, true, false);
        DirectExchange deadExchange = new DirectExchange(queueName + ".dlx", true, false);
        Queue queue = QueueBuilder.durable(queueName)
                .deadLetterExchange(deadExchange.getName()).deadLetterRoutingKey("failed").build();
        Queue deadQueue = QueueBuilder.durable(queueName + ".dlq").build();
        return new Declarables(exchange, deadExchange, queue, deadQueue,
                BindingBuilder.bind(queue).to(exchange).with("order.assigned"),
                BindingBuilder.bind(queue).to(exchange).with("order.delivered"),
                BindingBuilder.bind(queue).to(exchange).with("ORDER_ASSIGNED"),
                BindingBuilder.bind(queue).to(exchange).with("ORDER_DELIVERED"),
                BindingBuilder.bind(deadQueue).to(deadExchange).with("failed"));
    }
}
