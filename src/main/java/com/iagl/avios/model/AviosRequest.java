package com.iagl.avios.model;

import com.iagl.avios.enums.CabinCode;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record AviosRequest(
    @NotBlank(message = "Departure airport code is required") String departureAirportCode,
    @NotBlank(message = "Arrival airport code is required") String arrivalAirportCode,
    @Nullable CabinCode cabinCode) {}
