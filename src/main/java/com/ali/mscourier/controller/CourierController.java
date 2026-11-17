package com.ali.mscourier.controller;

import com.ali.mscourier.dto.*;
import com.ali.mscourier.service.CourierService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping({"/couriers"})
@RequiredArgsConstructor
public class CourierController {
    private final CourierService courierService;

    @PostMapping
    public ResponseEntity<CourierResponseDto> createCourier(@Valid @RequestBody CreateCourierRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courierService.createCourier(request));
    }

    @GetMapping("/available")
    public ResponseEntity<CourierResponseDto> getAvailableCourier() {
        return courierService.getAvailableCourier().map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public CourierResponseDto get(@PathVariable Long id) { return courierService.getCourier(id); }

    @GetMapping
    public List<CourierResponseDto> list(@RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "50") @Min(1) @Max(100) int size) {
        return courierService.listCouriers(page, size);
    }

    @PutMapping("/{id}")
    public CourierResponseDto update(@PathVariable Long id, @Valid @RequestBody CreateCourierRequest request) {
        return courierService.updateCourier(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { courierService.deleteCourier(id); }
}
