package com.ali.mscourier.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderAssignedEvent {
    private String orderId;
    private String courierId;
}
