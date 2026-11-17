package com.ali.mscourier.repository;

import com.ali.mscourier.entity.CourierOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourierOrderRepository extends JpaRepository<CourierOrder, String> {

}
