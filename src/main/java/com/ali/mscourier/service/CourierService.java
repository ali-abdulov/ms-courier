package com.ali.mscourier.service;

import com.ali.mscourier.dto.CourierResponseDto;
import com.ali.mscourier.dto.CreateCourierRequest;
import com.ali.mscourier.entity.Courier;
import com.ali.mscourier.enums.CourierStatus;
import com.ali.mscourier.mapper.CourierMapper;
import com.ali.mscourier.repository.CourierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @CacheEvict(value = "available_couriers", allEntries = true)
    public CourierResponseDto createCourier(CreateCourierRequest request) {
        Courier courier = Courier.builder()
                .name(request.getName())
                .gender(request.getGender())
                .status(CourierStatus.FREE)
                .build();

        return courierMapper.toDto(courierRepository.save(courier));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "available_couriers", key = "'first_free'")
    public Optional<CourierResponseDto> getAvailableCourier() {
        return courierRepository.findFirstByStatus(CourierStatus.FREE)
                .map(courierMapper::toDto);
    }

    @Transactional
    @CacheEvict(value = "available_couriers", allEntries = true)
    public CourierResponseDto updateCourierStatus(Long courierId, CourierStatus status) {
        Courier courier = courierRepository.findById(courierId)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format(COURIER_NOT_FOUND)
                ));

        courier.setStatus(status);
        return courierMapper.toDto(courierRepository.save(courier));
    }
}