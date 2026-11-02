package com.ali.mscourier.dto;

import com.ali.mscourier.enums.CourierStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourierResponseDto {
    private Long id;
    private String name;
    private CourierStatus status;
}