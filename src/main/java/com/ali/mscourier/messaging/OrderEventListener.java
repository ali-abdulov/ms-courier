package com.ali.mscourier.messaging;

import com.ali.mscourier.enums.CourierStatus;
import com.ali.mscourier.messaging.event.OrderAssignedEvent;
import com.ali.mscourier.messaging.event.OrderDeliveredEvent;
import com.ali.mscourier.service.CourierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.ali.mscourier.constant.RabbitConstants.COURIER_ASSIGNED_QUEUE;
import static com.ali.mscourier.constant.RabbitConstants.COURIER_DELIVERED_QUEUE;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final CourierService courierService;

}