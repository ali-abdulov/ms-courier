package com.ali.mscourier.mapper;

import com.ali.mscourier.dto.CourierResponseDto;
import com.ali.mscourier.entity.Courier;
import org.springframework.stereotype.Component;

@Component
public class CourierMapper {

    public CourierResponseDto toDto(Courier courier) {
        return CourierResponseDto.builder()
                .id(courier.getId())
                .name(courier.getName())
                .status(courier.getStatus())
                .build();
    }
}