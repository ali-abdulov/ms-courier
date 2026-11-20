package com.ali.mscourier.dto;

import com.ali.mscourier.enums.Gender;
import com.ali.mscourier.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateCourierRequest {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Fin code is required")
    @Size(min = 7, max = 7, message = "Fin code must be exactly 7 characters")
    private String finCode;

    @NotNull(message = "Gender is required")
    private Gender gender;

    private LocalDate dateOfBirth;

    private String address;

    @NotBlank(message = "Phone Number is required")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    private String email;

    @NotNull(message = "Vehicle type must be selected")
    private VehicleType vehicleType;
}