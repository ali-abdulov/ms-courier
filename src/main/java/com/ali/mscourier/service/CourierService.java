package com.ali.mscourier.service;

import com.ali.mscourier.dto.CourierResponseDto;
import com.ali.mscourier.dto.CreateCourierRequest;
import com.ali.mscourier.entity.Courier;
import com.ali.mscourier.enums.CourierStatus;
import com.ali.mscourier.mapper.CourierMapper;
import com.ali.mscourier.repository.CourierRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.ali.mscourier.constant.ExceptionMessages.COURIER_NOT_FOUND;
import static com.ali.mscourier.constant.ExceptionMessages.FIN_CODE_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class CourierService {

    private final CourierRepository courierRepository;
    private final CourierMapper courierMapper;


    @Transactional
    public CourierResponseDto createCourier(CreateCourierRequest request) {
        if (courierRepository.existsByFinCode(request.getFinCode())) {
            throw new IllegalArgumentException(FIN_CODE_ALREADY_EXISTS);
        }

        Courier courier = Courier.builder()
                .name(request.getName())
                .finCode(request.getFinCode())
                .gender(request.getGender())
                .birthDate(request.getDateOfBirth())
                .address(request.getAddress())
                .vehicleType(request.getVehicleType())
                .status(CourierStatus.FREE)
                .build();

        return courierMapper.toDto(courierRepository.save(courier));
    }

    public CourierResponseDto findCourierById(Long id){
        if  (courierRepository.existsById(id)) {
            return courierMapper.toDto(courierRepository.findById(id).get());
        }
        throw  new IllegalArgumentException(COURIER_NOT_FOUND);
    }

}