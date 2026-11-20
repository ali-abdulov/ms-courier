package com.ali.mscourier.controller;

import com.ali.mscourier.dto.CourierResponseDto;
import com.ali.mscourier.dto.CreateCourierRequest;
import com.ali.mscourier.enums.CourierStatus;
import com.ali.mscourier.service.CourierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/couriers")
@RequiredArgsConstructor
public class CourierController {

    private final CourierService courierService;

    @PostMapping
    @ResponseStatus(CREATED)
    public CourierResponseDto createCourier(@Valid @RequestBody CreateCourierRequest request) {
        return courierService.createCourier(request);
    }

    @GetMapping("/{id}")
    public CourierResponseDto findCourierById(@Valid @PathVariable Long id) {
        return courierService.findCourierById(id);
    }
}