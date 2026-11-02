package com.ali.mscourier.service;

import com.ali.mscourier.dto.CourierResponseDto;
import com.ali.mscourier.dto.CreateCourierRequest;
import com.ali.mscourier.entity.Courier;
import com.ali.mscourier.enums.CourierStatus;
import com.ali.mscourier.mapper.CourierMapper;
import com.ali.mscourier.repository.CourierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.ali.mscourier.constant.ExceptionMessages.COURIER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CourierService {

    private final CourierRepository courierRepository;
    private final CourierMapper courierMapper;

    @Transactional
    public CourierResponseDto createCourier(CreateCourierRequest request) {
        Courier courier = Courier.builder()
                .name(request.getName())
                .status(CourierStatus.FREE)
                .build();

        return courierMapper.toDto(courierRepository.save(courier));
    }

    @Transactional(readOnly = true)
    public Optional<CourierResponseDto> getAvailableCourier() {
        return courierRepository.findFirstByStatus(CourierStatus.FREE)
                .map(courierMapper::toDto);
    }

    @Transactional
    public CourierResponseDto updateCourierStatus(Long courierId, CourierStatus status) {
        Courier courier = courierRepository.findById(courierId)
                .orElseThrow(() -> new IllegalArgumentException(
                        COURIER_NOT_FOUND
                ));

        courier.setStatus(status);
        return courierMapper.toDto(courierRepository.save(courier));
    }
}