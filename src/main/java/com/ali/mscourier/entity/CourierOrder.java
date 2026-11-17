package com.ali.mscourier.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "courier_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourierOrder {
    @Id
    @Column(length = 100)
    private String orderId;
    @Column(nullable = false)
    private Long courierId;
    @Column(nullable = false)
    private boolean delivered;
}
