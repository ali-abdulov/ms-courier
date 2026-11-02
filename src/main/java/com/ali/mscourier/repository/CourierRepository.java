package com.ali.mscourier.repository;

import com.ali.mscourier.entity.Courier;
import com.ali.mscourier.enums.CourierStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourierRepository extends JpaRepository<Courier, Long> {
    Optional<Courier> findFirstByStatus(CourierStatus status);
}