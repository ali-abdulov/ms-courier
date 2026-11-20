package com.ali.mscourier.repository;

import com.ali.mscourier.entity.Courier;
import com.ali.mscourier.enums.CourierStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourierRepository extends JpaRepository<Courier, Long> {
    Optional<Courier> findFirstByStatus(CourierStatus status);

    boolean existsByFinCode(@NotBlank(message = "Fin code is required") @Size(min = 7, max = 7, message = "Fin code must be exactly 7 characters") String finCode);
}