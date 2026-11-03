package com.ali.mscourier.messaging;

import com.ali.mscourier.enums.CourierStatus;
import com.ali.mscourier.messaging.event.OrderAssignedEvent;
import com.ali.mscourier.messaging.event.OrderDeliveredEvent;
import com.ali.mscourier.service.CourierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final CourierService courierService;

    @RabbitListener(queues = RabbitMQConfig.COURIER_ASSIGNED_QUEUE)
    public void handleOrderAssigned(OrderAssignedEvent event) {
        log.info("Received ORDER_ASSIGNED event for orderId: {}, courierId: {}", event.getOrderId(), event.getCourierId());
        courierService.updateCourierStatus(Long.valueOf(event.getCourierId()), CourierStatus.BUSY);
    }

    @RabbitListener(queues = RabbitMQConfig.COURIER_DELIVERED_QUEUE)
    public void handleOrderDelivered(OrderDeliveredEvent event) {
        log.info("Received ORDER_DELIVERED event for orderId: {}, courierId: {}", event.getOrderId(), event.getCourierId());
        courierService.updateCourierStatus(Long.valueOf(event.getCourierId()), CourierStatus.FREE);
    }
}