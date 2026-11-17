package com.ali.mscourier.service;

import static com.ali.mscourier.constant.ExceptionMessages.CANNOT_DELETE_BUSY_COURIER;
import static com.ali.mscourier.constant.ExceptionMessages.FIN_CODE_ALREADY_EXISTS;
import com.ali.mscourier.dto.*;
import com.ali.mscourier.entity.Courier;
import com.ali.mscourier.enums.CourierStatus;
import com.ali.mscourier.mapper.CourierMapper;
import com.ali.mscourier.repository.CourierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ali.mscourier.exception.*;

import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourierService {
    private final CourierRepository courierRepository;
    private final CourierMapper courierMapper;

    @Transactional
    public CourierResponseDto createCourier(CreateCourierRequest request) {
        if (courierRepository.existsByFinCode(request.getFinCode())) {
            throw new CourierConflictException(FIN_CODE_ALREADY_EXISTS);
        }
        Courier courier = new Courier();
        applyProfile(courier, request);
        courier.setStatus(CourierStatus.FREE);

        Courier saved = courierRepository.saveAndFlush(courier);
        log.info("Created courier with id={}", saved.getId()); // LOG
        return courierMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public Optional<CourierResponseDto> getAvailableCourier() {
        return courierRepository.findFirstByStatusOrderByIdAsc(CourierStatus.FREE).map(courierMapper::toDto);
    }

    @Transactional(readOnly = true)
    public CourierResponseDto getCourier(Long id) {
        return courierMapper.toDto(courierRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new CourierNotFoundException(id)));
    }

    @Transactional(readOnly = true)
    public List<CourierResponseDto> listCouriers(int page, int size) {
        return courierRepository.findAllByDeletedFalse(PageRequest.of(page, size, org.springframework.data.domain.Sort.by("id")))
                .map(courierMapper::toDto).getContent();
    }

    @Transactional
    public CourierResponseDto updateCourier(Long id, CreateCourierRequest request) {
        Courier courier = courierRepository.findLockedById(id).orElseThrow(() -> new CourierNotFoundException(id));
        if (courierRepository.existsByFinCodeAndIdNot(request.getFinCode(), id)) {
            throw new CourierConflictException("FIN code already exists");
        }
        applyProfile(courier, request);
        return courierMapper.toDto(courierRepository.saveAndFlush(courier));
    }

    @Transactional
    public void deleteCourier(Long id) {
        Courier courier = courierRepository.findLockedById(id).orElseThrow(() -> new CourierNotFoundException(id));
        if (courier.getStatus() == CourierStatus.BUSY) {
            throw new CourierConflictException(CANNOT_DELETE_BUSY_COURIER);
        }
        courier.setDeleted(true);
        log.info("Marked courier as deleted. courierId={}", id);
    }

    private void applyProfile(Courier courier, CreateCourierRequest request) {
        courier.setName(request.getName().trim());
        courier.setFinCode(request.getFinCode());
        courier.setGender(request.getGender());
        courier.setBirthDate(request.getDateOfBirth());
        courier.setAddress(request.getAddress());
    }
}

