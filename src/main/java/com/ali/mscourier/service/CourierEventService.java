package com.ali.mscourier.service;

import com.ali.mscourier.entity.*;
import com.ali.mscourier.exception.*;
import com.ali.mscourier.enums.CourierStatus;
import com.ali.mscourier.repository.*;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourierEventService {
    private final CourierRepository couriers;
    private final CourierOrderRepository orders;
    private final EntityManager entityManager;

    @Transactional
    public void apply(String orderId, Long courierId, boolean delivered) {
        if (orderId == null || orderId.isBlank() || orderId.length() > 100 || courierId == null || courierId <= 0) {
            throw new InvalidOrderEventException("Valid orderId and positive courierId are required");
        }
        Courier courier = couriers.findLockedById(courierId)
                .orElseThrow(() -> new CourierNotFoundException(courierId));
        CourierOrder previous = orders.findById(orderId).orElse(null);
        if (previous != null && !previous.getCourierId().equals(courierId)) {
            throw new InvalidOrderEventException("Order belongs to another courier");
        }
        if (previous != null && previous.isDelivered()) return;
        if (delivered) {
            if (previous == null) {
                entityManager.persist(new CourierOrder(orderId, courierId, true));
            } else {
                previous.setDelivered(true);
            }
            if (orderId.equals(courier.getActiveOrderId())) {
                courier.setStatus(CourierStatus.FREE);
                courier.setActiveOrderId(null);
            }
        } else {
            if (courier.getStatus() == CourierStatus.BUSY && !orderId.equals(courier.getActiveOrderId())) {
                throw new CourierConflictException("Courier is assigned to a different order");
            }
            if (previous == null) entityManager.persist(new CourierOrder(orderId, courierId, false));
            courier.setStatus(CourierStatus.BUSY);
            courier.setActiveOrderId(orderId);
        }
    }
}

