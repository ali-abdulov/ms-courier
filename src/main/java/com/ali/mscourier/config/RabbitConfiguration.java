package com.ali.mscourier.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.ali.mscourier.constant.RabbitConstants.*;

@Configuration
public class RabbitConfiguration {

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Queue assignedQueue() {
        return new Queue(COURIER_ASSIGNED_QUEUE);
    }

    @Bean
    public Queue deliveredQueue() {
        return new Queue(COURIER_DELIVERED_QUEUE);
    }

    @Bean
    public Binding bindAssigned(Queue assignedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(assignedQueue).to(orderExchange).with(ORDER_ASSIGNED);
    }

    @Bean
    public Binding bindDelivered(Queue deliveredQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(deliveredQueue).to(orderExchange).with(ORDER_DELIVERED);
    }

    @Bean
    public MessageConverter jsonConverter() {
        return new JacksonJsonMessageConverter();
    }
}