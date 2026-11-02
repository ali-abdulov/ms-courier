package com.ali.mscourier.controller;

import com.ali.mscourier.dto.CourierResponseDto;
import com.ali.mscourier.dto.CreateCourierRequest;
import com.ali.mscourier.enums.CourierStatus;
import com.ali.mscourier.service.CourierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders/couriers")
@RequiredArgsConstructor
public class CourierController {

    private final CourierService courierService;

    @PostMapping
    public ResponseEntity<CourierResponseDto> createCourier(@RequestBody CreateCourierRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courierService.createCourier(request));
    }

    @GetMapping("/available")
    public ResponseEntity<CourierResponseDto> getAvailableCourier() {
        return courierService.getAvailableCourier()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CourierResponseDto> updateStatus(
            @PathVariable Long id,
            @RequestParam CourierStatus status) {
        return ResponseEntity.ok(courierService.updateCourierStatus(id, status));
    }
}