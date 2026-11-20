package com.ali.mscourier.dto;

import com.ali.mscourier.enums.CourierStatus;
import com.ali.mscourier.enums.Gender;
import com.ali.mscourier.enums.VehicleType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CourierResponseDto {
    private Long id;
    private String name;
    private CourierStatus status;
    private VehicleType vehicleType;

}