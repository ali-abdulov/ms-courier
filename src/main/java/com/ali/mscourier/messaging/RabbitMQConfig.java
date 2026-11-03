package com.ali.mscourier.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_EXCHANGE = "order.exchange";

    public static final String COURIER_ASSIGNED_QUEUE = "courier.order.assigned.queue";
    public static final String COURIER_DELIVERED_QUEUE = "courier.order.delivered.queue";

    public static final String ROUTING_KEY_ASSIGNED = "order.assigned";
    public static final String ROUTING_KEY_DELIVERED = "order.delivered";

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Queue courierAssignedQueue() {
        return new Queue(COURIER_ASSIGNED_QUEUE, true);
    }

    @Bean
    public Queue courierDeliveredQueue() {
        return new Queue(COURIER_DELIVERED_QUEUE, true);
    }

    @Bean
    public Binding assignedBinding(Queue courierAssignedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(courierAssignedQueue).to(orderExchange).with(ROUTING_KEY_ASSIGNED);
    }

    @Bean
    public Binding deliveredBinding(Queue courierDeliveredQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(courierDeliveredQueue).to(orderExchange).with(ROUTING_KEY_DELIVERED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}