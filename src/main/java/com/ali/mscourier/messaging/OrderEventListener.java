package com.ali.mscourier.messaging;

import com.ali.mscourier.service.CourierEventService;
import com.ali.mscourier.exception.InvalidOrderEventException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
public class OrderEventListener {
    private final CourierEventService service;
    private final JsonMapper mapper = JsonMapper.builder().build();

    @RabbitListener(queues = "${courier.messaging.queue:courier.state.events}")
    public void receive(Message message) {
        String key = message.getMessageProperties().getReceivedRoutingKey();
        boolean delivered = "order.delivered".equals(key) || "ORDER_DELIVERED".equals(key);
        if (!delivered && !"order.assigned".equals(key) && !"ORDER_ASSIGNED".equals(key)) {
            throw new AmqpRejectAndDontRequeueException("Unsupported order event");
        }
        String orderId;
        Long courierId;
        try {
            var json = mapper.readTree(message.getBody());
            if (json == null || !json.isObject() || !json.hasNonNull("orderId") || !json.hasNonNull("courierId")) {
                throw new InvalidOrderEventException("Missing event IDs");
            }
            var orderNode = json.get("orderId");
            var courierNode = json.get("courierId");
            if ((!orderNode.isString() && !orderNode.isIntegralNumber())
                    || (!courierNode.isString() && !courierNode.isIntegralNumber())) {
                throw new InvalidOrderEventException("IDs must be strings or integers");
            }
            orderId = orderNode.asText();
            courierId = Long.valueOf(courierNode.asText());
            if (orderId.isBlank() || orderId.length() > 100 || courierId <= 0) {
                throw new InvalidOrderEventException("Invalid event IDs");
            }
        } catch (RuntimeException e) {
            throw new AmqpRejectAndDontRequeueException("Invalid order event JSON", e);
        }
        service.apply(orderId, courierId, delivered);
    }
}

